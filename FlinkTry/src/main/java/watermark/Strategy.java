package watermark;

import bean.WaterSensor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import source.StreamSource;

import java.time.Duration;

public class Strategy {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataGeneratorSource<WaterSensor> generatorSource =
                StreamSource.WaterSensorSource(100, 3);

        DataStreamSource<WaterSensor> streamSource =
                env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "WaterSensor");

        SingleOutputStreamOperator<String> process = streamSource
                // TODO 2. 指定 watermark策略
                .assignTimestampsAndWatermarks(BoundedOutOfOrder())
                // TODO 3.使用 事件时间语义 的窗口
                .windowAll(TumblingEventTimeWindows.of(Time.seconds(10)))
                .process(
                        new ProcessAllWindowFunction<WaterSensor, String, TimeWindow>() {
                            @Override
                            public void process(ProcessAllWindowFunction<WaterSensor, String, TimeWindow>.Context context, Iterable<WaterSensor> elements, Collector<String> out) {
                                long count = elements.spliterator().estimateSize();
                                long windowStartTs = context.window().getStart();
                                long windowEndTs = context.window().getEnd();
                                String windowStart = DateFormatUtils.format(windowStartTs, "yyyy-MM-dd HH:mm:ss.SSS");
                                String windowEnd = DateFormatUtils.format(windowEndTs, "yyyy-MM-dd HH:mm:ss.SSS");

                                out.collect(windowStart + "," + windowEnd + "：包含" + count + "条数据===>" + elements);
                            }
                        }
                );

        process.print();

        env.execute();
    }

    /**
     * @return 单调升序的水位线策略
     */
    private static WatermarkStrategy<WaterSensor> Monotonous() {
        return WatermarkStrategy
                // 1.1 指定watermark生成：升序的watermark，没有等待时间
                .<WaterSensor>forMonotonousTimestamps()
                // 1.2 指定 时间戳分配器，从数据中提取
                .withTimestampAssigner(
                        (element, recordTimestamp) -> {
                            // recordTimestamp 默认值为 NO_TIMESTAMP（=Long.MIN_VALUE: -9223372036854775808）。
                            System.out.println("数据=" + element + ",时间戳=" + recordTimestamp);
                            // 返回的时间戳，要 毫秒
                            return element.getTs() * 1000L;
                        }
                );
    }

    /**
     * @return 乱序的水位线策略
     */
    private static WatermarkStrategy<WaterSensor> BoundedOutOfOrder() {
        return WatermarkStrategy
                // 1.1 指定watermark生成：乱序的，等待3s
                .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                // 1.2 指定 时间戳分配器，从数据中提取
                .withTimestampAssigner(
                        (element, recordTimestamp) -> {
                            // recordTimestamp 默认值为 NO_TIMESTAMP（=Long.MIN_VALUE: -9223372036854775808）。
                            System.out.println("数据=" + element + ",时间戳=" + recordTimestamp);
                            // 返回的时间戳，要 毫秒
                            return element.getTs() * 1000L;
                        }
                );
    }
}
