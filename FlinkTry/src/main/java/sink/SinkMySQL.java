package sink;

import bean.WaterSensor;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class SinkMySQL {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
                new WaterSensor("sensor_1", 1L, 1),
                new WaterSensor("sensor_2", 2L, 2),
                new WaterSensor("sensor_3", 3L, 3)
        );

        /*
          TODO 写入mysql
          1、只能用老的sink写法： addSink
          2、JDBCSink的4个参数:
             第一个参数： 执行的sql，一般就是 insert into
             第二个参数： 预编译sql， 对占位符填充值
             第三个参数： 执行选项 ---》 攒批、重试
             第四个参数： 连接选项 ---》 url、用户名、密码
         */
        SinkFunction<WaterSensor> jdbcSink = JdbcSink.sink(
                "insert into ws values(?,?,?)",
                (preparedStatement, waterSensor) -> {
                    //每收到一条WaterSensor，如何去填充占位符
                    preparedStatement.setString(1, waterSensor.getId());
                    preparedStatement.setLong(2, waterSensor.getTs());
                    preparedStatement.setInt(3, waterSensor.getVc());
                },
                JdbcExecutionOptions.builder()
                        .withMaxRetries(3) // 重试次数
                        .withBatchSize(100) // 批次的大小：条数
                        .withBatchIntervalMs(3000) // 批次的时间
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://172.28.222.214:3306/Warehouse?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8")
                        .withUsername("root")
                        .withPassword("Qs23=zs32")
                        .withConnectionCheckTimeoutSeconds(60) // 重试的超时时间
                        .build()
        );

        sensorDS.addSink(jdbcSink);

        env.execute();
    }
}
