import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class JokerOptimizer {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int MAIN_NUMBERS = 5;
    private static final int MAX_MAIN_NUMBER = 45;
    private static final int MAX_JOKER_NUMBER = 20;

    public static List<JokerTicket> generateOptimizedTickets(
            int ticketCount,
            int simulations
    ) {
        if (ticketCount <= 0) {
            throw new IllegalArgumentException(
                    "ticketCount must be greater than 0"
            );
        }

        if (simulations <= 0) {
            throw new IllegalArgumentException(
                    "simulations must be greater than 0"
            );
        }

        List<JokerTicket> bestTickets = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < simulations; i++) {

            List<JokerTicket> candidate =
                    generateRandomTickets(ticketCount);

            double score = calculateScore(candidate);

            if (score > bestScore) {
                bestScore = score;
                bestTickets = candidate;
            }
        }

        System.out.printf(
                "Best score after %,d simulations: %.2f%n",
                simulations,
                bestScore
        );

        return bestTickets;
    }

    private static List<JokerTicket> generateRandomTickets(
            int ticketCount
    ) {
        List<JokerTicket> tickets = new ArrayList<>();
        Set<JokerTicket> uniqueTickets = new HashSet<>();

        while (uniqueTickets.size() < ticketCount) {

            List<Integer> numbers =
                    generateNumbers(MAIN_NUMBERS, MAX_MAIN_NUMBER);

            int jokerNumber =
                    RANDOM.nextInt(1, MAX_JOKER_NUMBER + 1);

            uniqueTickets.add(
                    new JokerTicket(numbers, jokerNumber)
            );
        }

        tickets.addAll(uniqueTickets);

        return tickets;
    }

    private static double calculateScore(
            List<JokerTicket> tickets
    ) {
        Set<Integer> distinctMainNumbers = new HashSet<>();
        Set<Integer> distinctJokerNumbers = new HashSet<>();

        for (JokerTicket ticket : tickets) {
            distinctMainNumbers.addAll(ticket.numbers());
            distinctJokerNumbers.add(ticket.jokerNumber());
        }

        double score = 0;

        // Reward main-number coverage
        score += distinctMainNumbers.size() * 10.0;

        // Reward different Joker numbers
        score += distinctJokerNumbers.size() * 15.0;

        // Penalize overlaps between main-number combinations
        for (int i = 0; i < tickets.size(); i++) {
            for (int j = i + 1; j < tickets.size(); j++) {

                int overlap = calculateOverlap(
                        tickets.get(i).numbers(),
                        tickets.get(j).numbers()
                );

                score -= switch (overlap) {
                    case 0 -> 0;
                    case 1 -> 2;
                    case 2 -> 10;
                    case 3 -> 30;
                    case 4 -> 100;
                    case 5 -> 500;
                    default -> 0;
                };
            }
        }

        return score;
    }

    private static int calculateOverlap(
            List<Integer> first,
            List<Integer> second
    ) {
        int overlap = 0;

        for (Integer number : first) {
            if (second.contains(number)) {
                overlap++;
            }
        }

        return overlap;
    }

    private static List<Integer> generateNumbers(
            int count,
            int maxNumber
    ) {
        List<Integer> pool = new ArrayList<>(
                IntStream.rangeClosed(1, maxNumber)
                        .boxed()
                        .toList()
        );

        Collections.shuffle(pool, RANDOM);

        return pool.stream()
                .limit(count)
                .sorted()
                .toList();
    }

    public record JokerTicket(
            List<Integer> numbers,
            int jokerNumber
    ) {
        @Override
        public String toString() {
            return numbers + " | Joker: " + jokerNumber;
        }
    }
}