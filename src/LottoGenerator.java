import java.security.SecureRandom;
import java.util.*;
import java.util.stream.IntStream;

public class LottoGenerator  {
    public static void main(String[] args) {

        Set<List<Integer>> tickets = new HashSet<>();
        while(tickets.size() < 3) {
            tickets.add(getTicket());
        }
        tickets.forEach(System.out::println);
    }

    private static List<Integer> getTicket() {
        List<Integer> numbers = new ArrayList<>(
                IntStream.rangeClosed(1,49)
                        .boxed()
                        .toList()
        );

        Collections.shuffle(numbers, new SecureRandom());

        return numbers.stream()
                .limit(6)
                .sorted()
                .toList();
    }
}