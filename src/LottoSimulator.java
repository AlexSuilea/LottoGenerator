import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SplittableRandom;

public final class LottoSimulator {

    private static final int NUMBERS_PER_DRAW = 6;
    private static final int MAX_NUMBER = 49;

    public static void runComparison(int ticketCount, int candidatesPerSmartTicket, int simulations, long seed) {
        if (ticketCount <= 0) {
            throw new IllegalArgumentException("ticketCount must be greater than 0");
        }

        if (candidatesPerSmartTicket <= 0) {
            throw new IllegalArgumentException("candidatesPerSmartTicket must be greater than 0");
        }

        if (simulations <= 0) {
            throw new IllegalArgumentException("simulations must be greater than 0");
        }

        List<List<Integer>> randomTickets = generateRandomTickets(ticketCount);
        List<List<Integer>> smartTickets = SmartTicketGenerator.generateDiversifiedTickets(ticketCount,
                candidatesPerSmartTicket);

        System.out.println();
        System.out.println("RANDOM tickets:");
        randomTickets.forEach(System.out::println);

        System.out.println();
        System.out.println("SMART tickets:");
        smartTickets.forEach(System.out::println);

        TicketAnalyzer.printAnalysis("RANDOM", randomTickets, MAX_NUMBER);
        TicketAnalyzer.printAnalysis("SMART", smartTickets, MAX_NUMBER);

        long[] randomResults = new long[NUMBERS_PER_DRAW + 1];
        long[] smartResults = new long[NUMBERS_PER_DRAW + 1];

        SplittableRandom simulationRandom = new SplittableRandom(seed);

        for (int i = 0; i < simulations; i++) {
            int[] winningNumbers = generateWinningNumbers(simulationRandom);
            int randomBestMatch = calculateBestMatch(randomTickets, winningNumbers);
            int smartBestMatch = calculateBestMatch(smartTickets, winningNumbers);

            randomResults[randomBestMatch]++;
            smartResults[smartBestMatch]++;
        }
        printResults(randomResults, smartResults, simulations, seed);
    }

    private static List<List<Integer>> generateRandomTickets(int ticketCount) {
        Set<List<Integer>> tickets = new LinkedHashSet<>();

        while (tickets.size() < ticketCount) {
            tickets.add(LottoMachine.generateTicket(NUMBERS_PER_DRAW, MAX_NUMBER));
        }

        return new ArrayList<>(tickets);
    }

    private static int[] generateWinningNumbers(SplittableRandom random) {
        int[] numbers = new int[NUMBERS_PER_DRAW];
        int count = 0;

        while (count < NUMBERS_PER_DRAW) {
            int candidate = random.nextInt(1, MAX_NUMBER + 1);

            if (!contains(numbers, count, candidate)) {
                numbers[count] = candidate;
                count++;
            }
        }

        Arrays.sort(numbers);

        return numbers;
    }

    private static boolean contains(int[] numbers, int length, int value) {
        for (int i = 0; i < length; i++) {
            if (numbers[i] == value) {
                return true;
            }
        }
        return false;
    }

    private static int calculateBestMatch(List<List<Integer>> tickets, int[] winningNumbers) {
        int bestMatch = 0;

        for (List<Integer> ticket : tickets) {
            int matches = calculateMatches(ticket, winningNumbers);
            bestMatch = Math.max(bestMatch, matches);

            if (bestMatch == NUMBERS_PER_DRAW) {
                break;
            }
        }

        return bestMatch;
    }

    private static int calculateMatches(List<Integer> ticket, int[] winningNumbers) {
        int ticketIndex = 0;
        int winningIndex = 0;
        int matches = 0;

        while (ticketIndex < ticket.size() && winningIndex < winningNumbers.length) {

            int ticketNumber = ticket.get(ticketIndex);
            int winningNumber = winningNumbers[winningIndex];

            if (ticketNumber == winningNumber) {
                matches++;
                ticketIndex++;
                winningIndex++;

            } else if (ticketNumber < winningNumber) {
                ticketIndex++;

            } else {
                winningIndex++;
            }
        }

        return matches;
    }

    private static void printResults(
            long[] randomResults,
            long[] smartResults,
            int simulations,
            long seed
    ) {
        System.out.println();
        System.out.println(
                "========== SIMULATION RESULTS =========="
        );

        System.out.println(
                "Simulations: " + simulations
        );

        System.out.println(
                "Seed: " + seed
        );

        System.out.println();

        System.out.printf(
                "%-10s %22s %22s%n",
                "Best hit",
                "RANDOM",
                "SMART"
        );

        for (int matches = 0;
             matches <= NUMBERS_PER_DRAW;
             matches++) {

            System.out.printf(
                    Locale.US,
                    "%-10s %10d (%9.6f%%) %10d (%9.6f%%)%n",
                    matches + "/6",
                    randomResults[matches],
                    percentage(
                            randomResults[matches],
                            simulations
                    ),
                    smartResults[matches],
                    percentage(
                            smartResults[matches],
                            simulations
                    )
            );
        }

        System.out.println();
        System.out.println("At least:");

        for (int minimumMatches = 3;
             minimumMatches <= NUMBERS_PER_DRAW;
             minimumMatches++) {

            long randomAtLeast =
                    countAtLeast(
                            randomResults,
                            minimumMatches
                    );

            long smartAtLeast =
                    countAtLeast(
                            smartResults,
                            minimumMatches
                    );

            System.out.printf(
                    Locale.US,
                    "%d+/6 -> RANDOM: %d (%.6f%%) | SMART: %d (%.6f%%)%n",
                    minimumMatches,
                    randomAtLeast,
                    percentage(
                            randomAtLeast,
                            simulations
                    ),
                    smartAtLeast,
                    percentage(
                            smartAtLeast,
                            simulations
                    )
            );
        }
    }

    private static long countAtLeast(long[] results, int minimumMatches) {
        long total = 0;

        for (int i = minimumMatches; i < results.length; i++) {
            total += results[i];
        }

        return total;
    }

    private static double percentage(long count, long total) {
        return count * 100.0 / total;
    }

    private LottoSimulator() {
    }
}