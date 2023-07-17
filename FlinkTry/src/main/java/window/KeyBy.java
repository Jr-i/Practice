package window;

import bean.WaterSensor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KeyBy {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<WaterSensor> stream = env.fromElements(
                new WaterSensor("sensor_1", 1L, 1),
                new WaterSensor("sensor_2", 2L, 2),
                new WaterSensor("sensor_3", 2L, 2),
                new WaterSensor("sensor_1", 3L, 3)
        );

        // 方式一：使用Lambda表达式
        stream.keyBy(WaterSensor::getId).sum("vc").print();

        // 方式二：使用匿名类实现KeySelector
/*        KeyedStream<WaterSensor, String> keyedStream1 = stream.keyBy(new KeySelector<WaterSensor, String>() {
            @Override
            public String getKey(WaterSensor e) {
                return e.getId();
            }
        });*/

        env.execute();
    }
}
