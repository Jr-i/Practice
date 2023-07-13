package transfer;

import bean.WaterSensor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class Flatmap {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<WaterSensor> stream = env.fromElements(

                new WaterSensor("sensor_1", 1L, 1),
                new WaterSensor("sensor_2", 2L, 2),
                new WaterSensor("sensor_3", 3L, 3)

        );

        // 方法一 lambda表达式
        stream.flatMap((WaterSensor value, Collector<String> out) -> {
            if (value.getId().equals("sensor_1")) {
                out.collect(String.valueOf(value.getId()));
            } else if (value.getId().equals("sensor_2")) {
                out.collect(String.valueOf(value.getTs()));
                out.collect(String.valueOf(value.getVc()));
            }
        }).returns(Types.STRING).print();

        // 方法二 匿名实现类
/*        stream.flatMap(
                new FlatMapFunction<WaterSensor, String>() {
                    @Override
                    public void flatMap(WaterSensor value, Collector<String> out) {
                        if (value.getId().equals("sensor_1")) {
                            out.collect(String.valueOf(value.getId()));
                        } else if (value.getId().equals("sensor_2")) {
                            out.collect(String.valueOf(value.getTs()));
                            out.collect(String.valueOf(value.getVc()));
                        }
                    }
                }
        ).print();*/

        env.execute();

    }
}
