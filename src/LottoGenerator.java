import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class LottoGenerator  {
    public static void main(String[] args) {
        List<Integer> ticketA = getTicket();
        List<Integer> ticketB = getTicket();
        List<Integer> ticketC = getTicket();

        System.out.println("Zona A: " + ticketA);
        System.out.println("Zona B: " + ticketB);
        System.out.println("Zona C: " + ticketC);
    }

    private static List<Integer> getTicket() {
        List<Integer> numbers = new ArrayList<>(
                IntStream.rangeClosed(1,49)
                        .boxed()
                        .toList()
        );

        Collections.shuffle(numbers);

        return numbers.stream()
                .limit(6)
                .sorted()
                .toList();
    }
}