package sink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import source.StreamSource;

import java.time.Duration;
import java.time.ZoneId;

public class SinkFile {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 并行度 会影响 写入 文件个数
        env.setParallelism(2);
        // 在 STREAMING 模式下使用 FileSink 需要开启 Checkpoint 功能。 文件只在 Checkpoint 成功时生成。
        // 如果没有开启 Checkpoint 功能，文件将永远停留在 in-progress 或者 pending 的状态，
        // 并且下游系统将不能安全读取该文件数据。
        env.enableCheckpointing(2000, CheckpointingMode.EXACTLY_ONCE);

        DataGeneratorSource<String> dataGeneratorSource =
                StreamSource.DataGenSource(100, 3);

        DataStreamSource<String> dataGen =
                env.fromSource(dataGeneratorSource, WatermarkStrategy.noWatermarks(), "Generator");

        // 输出到文件系统
        FileSink<String> fieSink = FileSink
                // 输出行式存储的文件，指定路径、指定编码
                .<String>forRowFormat(new Path("FlinkTry/src/main/resources/"), new SimpleStringEncoder<>("UTF-8"))
                // 输出文件的一些配置： 文件名的前缀、后缀
                .withOutputFileConfig(
                        OutputFileConfig.builder()
                                .withPartPrefix("Generator-")
                                .withPartSuffix(".log")
                                .build()
                )
                // 按照目录分桶：如下，就是每个小时一个目录
                .withBucketAssigner(new DateTimeBucketAssigner<>("yyyy-MM-dd HH", ZoneId.systemDefault()))
                // 文件滚动策略:
                // 包含了至少1分钟的数据量
                // 超过2分钟没有收到新记录
                // 文件大小已经达到 1MB（写入最后一条记录之后）
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(Duration.ofMinutes(1))
                                .withInactivityInterval(Duration.ofMinutes(2))
                                .withMaxPartSize(MemorySize.ofMebiBytes(1))
                                .build()
                )
                .build();


        dataGen.sinkTo(fieSink);

        env.execute();
    }
}
