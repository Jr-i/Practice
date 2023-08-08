package watermark;

import bean.WaterSensor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import source.StreamSource;

public class Idleness {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataGeneratorSource<WaterSensor> generatorSource =
                StreamSource.WaterSensorSource(100, 3);

        DataStreamSource<WaterSensor> streamSource = env
                .fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "WaterSensor")
                .setParallelism(1);

        SingleOutputStreamOperator<WaterSensor> filterStream = streamSource
                // 确保 ts 为奇数的都在一个分区，ts 为偶数的都在另一个分区
                .keyBy(r -> r.getTs() % 2)
                // 过滤掉 ts 为奇数的 WaterSensor,下游只能够获得一个分区的数据
                .filter(e -> e.getTs() % 2 == 0)
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                // 1.1 指定watermark生成：升序的watermark，没有等待时间
                                .<WaterSensor>forMonotonousTimestamps()
                                // 1.2 指定 时间戳分配器，从数据中提取
                                .withTimestampAssigner(
                                        (element, recordTimestamp) -> element.getTs() * 1000L
                                )
                        // 当一个任务接收到多个上游并行任务传递来的水位线时，应该以最小的那个作为当前任务的事件时钟。
                        // 如果有其中一个没有数据，就会导致当前Task的水位线无法推进，就可能导致窗口无法触发。
                        // 这时候可以设置空闲等待。
//                                .withIdleness(Duration.ofSeconds(5))
                );

        filterStream.print();

        // 上游的 filter 是一个算子，下游的 process 是另一个算子
        filterStream
                .keyBy(r -> r.getTs() % 2)
                // 10s的事件时间滚动窗口
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .process(new ProcessWindowFunction<WaterSensor, String, Long, TimeWindow>() {
                    @Override
                    public void process(Long aLong, ProcessWindowFunction<WaterSensor, String, Long, TimeWindow>.Context context, Iterable<WaterSensor> elements, Collector<String> out) throws Exception {
                        long count = elements.spliterator().estimateSize();
                        long windowStartTs = context.window().getStart();
                        long windowEndTs = context.window().getEnd();
                        String windowStart = DateFormatUtils.format(windowStartTs, "yyyy-MM-dd HH:mm:ss.SSS");
                        String windowEnd = DateFormatUtils.format(windowEndTs, "yyyy-MM-dd HH:mm:ss.SSS");
                        out.collect("key=" + aLong + "的窗口[" + windowStart + "," + windowEnd + ")包含" + count + "条数据===>" + elements.toString());
                    }

                })
                .print();

        env.execute();
    }
}
