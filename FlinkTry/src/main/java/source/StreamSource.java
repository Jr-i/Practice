package source;

import bean.WaterSensor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class StreamSource {
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
                .setBootstrapServers("hadoop102:9092")
                .setTopics("first")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka")
                .print();

    }

    /**
     * 数据生成
     * @param numberOfRecords  数据生成总条数
     * @param recordsPerSecond 每秒生成数据条数
     * @return 流式数据源
     */
    public static DataGeneratorSource<String> DataGenSource(long numberOfRecords, double recordsPerSecond) {

        GeneratorFunction<Long, String> generatorFunction = index -> "Number: " + index;

        return new DataGeneratorSource<>(
                generatorFunction,
                numberOfRecords,
                RateLimiterStrategy.perSecond(recordsPerSecond),
                Types.STRING);
    }

    public static DataGeneratorSource<WaterSensor> WaterSensorSource(long numberOfRecords, double recordsPerSecond) {

        GeneratorFunction<Long, WaterSensor> generatorFunction = i ->
                new WaterSensor("sensor_" + i, i , (int) (i * i));

        return new DataGeneratorSource<>(
                generatorFunction,
                numberOfRecords,
                RateLimiterStrategy.perSecond(recordsPerSecond),
                Types.POJO(WaterSensor.class));
    }
}
