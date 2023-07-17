import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Test;
import source.StreamSource;

public class StreamSourceTest {

    @Test
    public void dataGenSource() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataGeneratorSource<String> source = StreamSource.DataGenSource(100, 3);

        env.fromSource(source, WatermarkStrategy.noWatermarks(), "Generator")
                .print();

        env.execute();
    }
}