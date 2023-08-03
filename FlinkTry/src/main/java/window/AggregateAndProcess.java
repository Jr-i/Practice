package window;

import bean.WaterSensor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import source.StreamSource;

public class AggregateAndProcess {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataGeneratorSource<WaterSensor> generatorSource =
                StreamSource.WaterSensorSource(100, 3);

        DataStreamSource<WaterSensor> streamSource =
                env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "WaterSensor");

        AllWindowedStream<WaterSensor, TimeWindow> sensorWS =
                streamSource.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        /*
          增量聚合 Aggregate + 全窗口 process
          1、增量聚合函数处理数据： 来一条计算一条
          2、窗口触发时， 增量聚合的结果（只有一条） 传递给 全窗口函数
          3、经过全窗口函数的处理包装后，输出

          结合两者的优点：
          1、增量聚合： 来一条计算一条，存储中间的计算结果，占用的空间少
          2、全窗口函数： 可以通过 上下文 实现灵活的功能
         */

        // reduce 也可以和 Process 配合使用
/*        SingleOutputStreamOperator<String> result = sensorWS.reduce(
                new MyReduce(),
                new MyProcess()
        );*/
        SingleOutputStreamOperator<String> result = sensorWS.aggregate(
                new MyAgg(),
                new MyProcess()
        );

        result.print();

        env.execute();
    }

    public static class MyAgg implements AggregateFunction<WaterSensor, Integer, String> {
        @Override
        public Integer createAccumulator() {
            System.out.println("初始化累加器");
            return 0;
        }

        @Override
        public Integer add(WaterSensor value, Integer accumulator) {
            System.out.println("调用add方法,value=" + value);
            return accumulator + value.getVc();
        }

        @Override
        public String getResult(Integer accumulator) {
            System.out.println("窗口触发时输出结果");
            return accumulator.toString();
        }

        @Override
        // 只有会话窗口才会用到
        public Integer merge(Integer a, Integer b) {
            System.out.println("调用merge方法");
            return null;
        }
    }

    // 将MyAgg的输出结果，作为MyProcess的输入内容
    // 全窗口函数的输入类型 = 增量聚合函数的输出类型
    public static class MyProcess extends ProcessAllWindowFunction<String, String, TimeWindow> {
        @Override
        public void process(ProcessAllWindowFunction<String, String, TimeWindow>.Context context, Iterable<String> elements, Collector<String> out) {
            long count = elements.spliterator().estimateSize();
            long windowStartTs = context.window().getStart();
            long windowEndTs = context.window().getEnd();
            String windowStart = DateFormatUtils.format(windowStartTs, "yyyy-MM-dd HH:mm:ss.SSS");
            String windowEnd = DateFormatUtils.format(windowEndTs, "yyyy-MM-dd HH:mm:ss.SSS");

            out.collect(windowStart + "," + windowEnd + "：包含" + count + "条数据===>" + elements);
        }
    }

}
