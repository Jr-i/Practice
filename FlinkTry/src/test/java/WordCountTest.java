import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class WordCountTest {
    @Test
    public void test9(){
        List<String> strings = Arrays.asList("aa", "bb", "cc", "dd");

        Stream<Stream<Character>> stream1 = strings.stream().map(s -> stringToStream(s));
        stream1.forEach(s -> s.forEach(System.out::println));

        System.out.println();

        Stream<Character> stream2 = strings.stream().flatMap(s -> stringToStream(s));
        stream2.forEach(System.out::println);
    }
    public Stream<Character> stringToStream(String s){
        ArrayList<Character> characters = new ArrayList<>();
        for (char c : s.toCharArray()) {
            characters.add(c);
        }
        return characters.stream();
    }
}