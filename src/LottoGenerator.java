import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class LottoGenerator  {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(
                IntStream.rangeClosed(1,49)
                        .boxed()
                        .toList()
        );

        Collections.shuffle(numbers);

        List<Integer> ticket = numbers.stream()
                .limit(6)
                .sorted()
                .toList();

        System.out.println("Numerele câștigătoare 😄: " + ticket);
    }
}