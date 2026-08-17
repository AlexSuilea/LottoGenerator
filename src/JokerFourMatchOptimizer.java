import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class JokerFourMatchOptimizer {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int MAIN_NUMBERS = 5;
    private static final int MAX_MAIN_NUMBER = 45;
    private static final int MAX_JOKER_NUMBER = 20;

    private static final int TOTAL_MAIN_COMBINATIONS = 1_221_759;

    public static Result optimize(
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

        List<List<Integer>> bestMainTickets = null;
        int bestCoverage = -1;

        for (int i = 0; i < simulations; i++) {

            List<List<Integer>> candidate =
                    generateMainTickets(ticketCount);

            int coverage =
                    calculateFourPlusCoverage(candidate);

            if (coverage > bestCoverage) {
                bestCoverage = coverage;
                bestMainTickets = candidate;
            }
        }

        List<JokerTicket> tickets = assignJokers(bestMainTickets);

        double coveragePercentage =
                bestCoverage * 100.0
                        / TOTAL_MAIN_COMBINATIONS;

        return new Result(
                tickets,
                bestCoverage,
                coveragePercentage
        );
    }

    private static List<List<Integer>> generateMainTickets(
            int ticketCount
    ) {
        Set<List<Integer>> tickets =
                new LinkedHashSet<>();

        while (tickets.size() < ticketCount) {
            tickets.add(generateTicket());
        }

        return new ArrayList<>(tickets);
    }

    private static List<Integer> generateTicket() {

        List<Integer> numbers = new ArrayList<>(
                IntStream.rangeClosed(1, MAX_MAIN_NUMBER)
                        .boxed()
                        .toList()
        );

        Collections.shuffle(numbers, RANDOM);

        return numbers.stream()
                .limit(MAIN_NUMBERS)
                .sorted()
                .toList();
    }

    /*
     * Counts every possible winning 5-number draw
     * for which at least one ticket would have
     * 4/5 or 5/5 main numbers.
     */
    private static int calculateFourPlusCoverage(
            List<List<Integer>> tickets
    ) {
        Set<Long> coveredWinningDraws =
                new HashSet<>();

        for (List<Integer> ticket : tickets) {
            addCoveredWinningDraws(
                    ticket,
                    coveredWinningDraws
            );
        }

        return coveredWinningDraws.size();
    }

    private static void addCoveredWinningDraws(
            List<Integer> ticket,
            Set<Long> covered
    ) {

        /*
         * A 5-number ticket contains
         * 5 different subsets of 4 numbers.
         */
        for (int omitted = 0;
             omitted < MAIN_NUMBERS;
             omitted++) {

            int[] fourNumbers = new int[4];
            boolean[] included =
                    new boolean[MAX_MAIN_NUMBER + 1];

            int index = 0;

            for (int i = 0; i < MAIN_NUMBERS; i++) {

                if (i == omitted) {
                    continue;
                }

                int number = ticket.get(i);

                fourNumbers[index++] = number;
                included[number] = true;
            }

            /*
             * Any winning draw containing these
             * four numbers gives us minimum 4/5.
             */
            for (int extra = 1;
                 extra <= MAX_MAIN_NUMBER;
                 extra++) {

                if (included[extra]) {
                    continue;
                }

                int[] winningDraw = {
                        fourNumbers[0],
                        fourNumbers[1],
                        fourNumbers[2],
                        fourNumbers[3],
                        extra
                };

                Arrays.sort(winningDraw);

                covered.add(
                        encodeCombination(winningDraw)
                );
            }
        }
    }

    /*
     * Converts a sorted combination into
     * one unique long value.
     */
    private static long encodeCombination(
            int[] numbers
    ) {
        long result = 0;

        for (int number : numbers) {
            result = result * 46 + number;
        }

        return result;
    }

    private static List<JokerTicket> assignJokers(
            List<List<Integer>> mainTickets
    ) {
        List<Integer> assignedJokers = new ArrayList<>();

        while (assignedJokers.size() < mainTickets.size()) {

            List<Integer> jokerPool = new ArrayList<>(
                    IntStream.rangeClosed(1, MAX_JOKER_NUMBER)
                            .boxed()
                            .toList()
            );

            Collections.shuffle(jokerPool, RANDOM);

            int remaining =
                    mainTickets.size() - assignedJokers.size();

            int numbersToAdd =
                    Math.min(remaining, MAX_JOKER_NUMBER);

            assignedJokers.addAll(
                    jokerPool.subList(0, numbersToAdd)
            );
        }

        List<JokerTicket> result = new ArrayList<>();

        for (int i = 0; i < mainTickets.size(); i++) {
            result.add(
                    new JokerTicket(
                            mainTickets.get(i),
                            assignedJokers.get(i)
                    )
            );
        }

        return result;
    }

    public record JokerTicket(
            List<Integer> numbers,
            int joker
    ) {
        @Override
        public String toString() {
            return numbers
                    + " | Joker: "
                    + joker;
        }
    }

    public record Result(
            List<JokerTicket> tickets,
            int fourPlusCoverage,
            double coveragePercentage
    ) {

        public void print() {

            System.out.println("Joker:");

            tickets.forEach(System.out::println);

            System.out.println();

            System.out.println(
                    "4+ coverage: "
                            + fourPlusCoverage
                            + " / "
                            + TOTAL_MAIN_COMBINATIONS
            );

            System.out.printf(
                    "Probability of 4+ main numbers: %.4f%%%n",
                    coveragePercentage
            );
        }
    }
}