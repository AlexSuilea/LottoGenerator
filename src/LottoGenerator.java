import java.security.SecureRandom;
import java.util.*;
import java.util.stream.IntStream;

public class LottoGenerator  {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static void main(String[] args) {
        printTickets("Loto 6/49:", 3, 6, 49);
        printTickets("Loto 5/40:", 4, 5, 40);
        getJokerTickets(2);
    }

    private static void printTickets(String game, int ticketCount, int numbers, int maxNumber) {
        if(ticketCount <= 0) {
            return;
        }
        System.out.println(game);
        Set<List<Integer>> tickets = new LinkedHashSet<>();
        while (tickets.size() < ticketCount) {
            tickets.add(getTicket(numbers, maxNumber));
        }
        tickets.forEach(System.out::println);
        System.out.println();
    }

    private static void getJokerTickets(int numberOfTickets) {
        if(numberOfTickets <= 0) {
            return;
        }
        System.out.println("Joker:");
        Set<String> jokerTickets = new HashSet<>();
        while(jokerTickets.size() < numberOfTickets) {
            int jokerNumber = getJokerNumber();
            jokerTickets.add(getTicket(5, 45) + " | Joker: " + jokerNumber);
        }
        jokerTickets.forEach(System.out::println);
        System.out.println();
    }

    private static int getJokerNumber() {
        return RANDOM.nextInt(20) + 1;
    }

    private static List<Integer> getTicket(int numberOfNumbers, int maxNumber) {
        if (numberOfNumbers <= 0 || numberOfNumbers > maxNumber) {
            throw new IllegalArgumentException(
                    "numberOfNumbers must be between 1 and maxNumber"
            );
        }
        List<Integer> numbers = new ArrayList<>(
                IntStream.rangeClosed(1,maxNumber)
                        .boxed()
                        .toList()
        );
        Collections.shuffle(numbers, RANDOM);
        return numbers.stream()
                .limit(numberOfNumbers)
                .sorted()
                .toList();
    }
}