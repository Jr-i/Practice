package window;

import bean.WaterSensor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import source.StreamSource;

public class Reduce {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataGeneratorSource<WaterSensor> generatorSource =
                StreamSource.WaterSensorSource(100, 3);

        DataStreamSource<WaterSensor> streamSource =
                env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "WaterSensor");

        streamSource
//                .keyBy(WaterSensor::getId)
                // 设置滚动事件时间窗口
                .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .reduce((value1, value2) -> {
                    System.out.println(value1 + "," + value2);
                    return new WaterSensor(value1.getId(), System.currentTimeMillis(), value1.getVc() + value2.getVc());
                })
                .print();

        env.execute();
    }
}
