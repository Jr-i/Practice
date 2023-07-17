package window;

import bean.WaterSensor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class Split {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<WaterSensor> stream = env.fromElements(
                new WaterSensor("sensor_1", 1L, 1),
                new WaterSensor("sensor_2", 2L, 2),
                new WaterSensor("sensor_3", 2L, 2),
                new WaterSensor("sensor_1", 3L, 3)
        );

        //通过标签定义一个侧输出流
        OutputTag<WaterSensor> sensor_1 = new OutputTag<>("sensor_1", Types.POJO(WaterSensor.class));

        SingleOutputStreamOperator<WaterSensor> outputStream = stream.process(new ProcessFunction<WaterSensor, WaterSensor>() {
            @Override
            public void processElement(WaterSensor value, ProcessFunction<WaterSensor, WaterSensor>.Context ctx, Collector<WaterSensor> out) throws Exception {
                if ("sensor_1".equals(value.getId())) {
                    //将WaterSensor放到指定的侧输出流中
                    ctx.output(sensor_1, value);
                }
                // 其他的放到主流中
                else {
                    out.collect(value);
                }
            }
        });

        //从主流中，根据标签获取侧输出流
        outputStream.getSideOutput(sensor_1).print("sensor_1");
        //获取主流
        outputStream.print("others");

        env.execute();


    }
}
