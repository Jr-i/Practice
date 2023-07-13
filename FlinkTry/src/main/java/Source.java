import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Source {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        kafkaSource(env);

        env.execute();
    }

    public static void fileSource(StreamExecutionEnvironment env) {

        FileSource<String> source = FileSource.forRecordStreamFormat(
                new TextLineInputFormat(), new Path("FlinkTry/src/main/resources/word.txt")
        ).build();

        env.fromSource(source, WatermarkStrategy.noWatermarks(), "file")
                .print();

    }

    public static void kafkaSource(StreamExecutionEnvironment env) {
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop103:9092")
                .setTopics("first")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka")
                .print();

    }

}
