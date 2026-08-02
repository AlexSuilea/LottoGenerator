import java.security.SecureRandom;
import java.util.*;
import java.util.stream.IntStream;

public class LottoGenerator  {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static void main(String[] args) {
        System.out.println("Loto 6/49:");
        getLotto649Tickets(3);

        System.out.println("Joker:");
        getJokerTickets(2);

        System.out.println("Loto 5/40:");
        getLotto540Tickets(4);
    }

    private static void getLotto649Tickets(int numberOfTickets) {
        Set<List<Integer>> lotto649Tickets = new HashSet<>();
        while(lotto649Tickets.size() < numberOfTickets) {
            lotto649Tickets.add(getTicket(6,49));
        }
        lotto649Tickets.forEach(System.out::println);
    }

    private static void getJokerTickets(int numberOfTickets) {
        Set<String> jokerTickets = new HashSet<>();
        while(jokerTickets.size() < numberOfTickets) {
            int jokerNumber = getJokerNumber();
            jokerTickets.add(getTicket(5, 45) + " | Joker: " + jokerNumber);
        }
        jokerTickets.forEach(System.out::println);
    }

    private static int getJokerNumber() {
        return RANDOM.nextInt(20) + 1;
    }

    private static void getLotto540Tickets(int numberOfTickets) {
        Set<List<Integer>> lotto540Tickets = new HashSet<>();
        while(lotto540Tickets.size() < numberOfTickets) {
            lotto540Tickets.add(getTicket(5,40));
        }
        lotto540Tickets.forEach(System.out::println);
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