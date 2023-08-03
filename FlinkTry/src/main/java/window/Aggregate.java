package window;

import bean.WaterSensor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import source.StreamSource;

public class Aggregate {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataGeneratorSource<WaterSensor> generatorSource =
                StreamSource.WaterSensorSource(100, 3);

        DataStreamSource<WaterSensor> streamSource =
                env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "WaterSensor");

        // 1. 窗口分配器
        AllWindowedStream<WaterSensor, TimeWindow> sensorWS =
                streamSource.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        sensorWS
                .aggregate(
                        new AggregateFunction<WaterSensor, Integer, String>() {
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
                ).print();

        env.execute();
    }
}
