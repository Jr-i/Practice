import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCount {
    public static void main(String[] args) throws Exception {

        // 1. 创建流式执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 测试用：有WebUI的本地环境
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        // 2. 读取文件
        DataStreamSource<String> lineStream = env.socketTextStream("hadoop102", 7777);
//        DataStreamSource<String> lineStream = env.readTextFile("FlinkTry/src/main/resources/word.txt");

        // 3. 转换、分组、求和，得到统计结果
        SingleOutputStreamOperator<Tuple2<String, Long>> sum = lineStream.flatMap(
                        (String line, Collector<Tuple2<String, Long>> out) -> {
                            String[] words = line.split(" ");
                            for (String word : words) {
                                out.collect(Tuple2.of(word, 1L));
                            }
                        }).returns(Types.TUPLE(Types.STRING, Types.LONG))
                .keyBy(data -> data.f0).sum(1);

        // 4. 打印
        sum.print();

        // 5. 执行
        env.execute();

    }

}
