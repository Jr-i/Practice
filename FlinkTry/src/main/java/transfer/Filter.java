package transfer;

import bean.WaterSensor;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Filter {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<WaterSensor> stream = env.fromElements(

                new WaterSensor("sensor_1", 1L, 1),
                new WaterSensor("sensor_1", 2L, 2),
                new WaterSensor("sensor_2", 2L, 2),
                new WaterSensor("sensor_3", 3L, 3)
        );

        // 方式一：传入匿名类实现FilterFunction
        stream.filter(e -> "sensor_1".equals(e.getId())).print();

        // 方式二：传入FilterFunction实现类
        // stream.filter(new UserFilter()).print();

        env.execute();
    }

    public static class UserFilter implements FilterFunction<WaterSensor> {
        @Override
        public boolean filter(WaterSensor e) throws Exception {
            return e.getId().equals("sensor_1");
        }
    }
}
