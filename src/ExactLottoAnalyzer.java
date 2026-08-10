import java.util.List;
import java.util.Locale;

public final class ExactLottoAnalyzer {

    private static final int MAX_NUMBER = 49;
    private static final int NUMBERS_PER_DRAW = 6;

    public static void compare(String firstStrategyName, List<List<Integer>> firstTickets, String secondStrategyName,
                               List<List<Integer>> secondTickets) {
        validateTickets(firstTickets);
        validateTickets(secondTickets);

        long[] firstMasks = convertToMasks(firstTickets);
        long[] secondMasks = convertToMasks(secondTickets);

        long[] firstResults = new long[NUMBERS_PER_DRAW + 1];
        long[] secondResults = new long[NUMBERS_PER_DRAW + 1];

        long totalDraws = 0;

        for (int a = 1; a <= 44; a++) {
            for (int b = a + 1; b <= 45; b++) {
                for (int c = b + 1; c <= 46; c++) {
                    for (int d = c + 1; d <= 47; d++) {
                        for (int e = d + 1; e <= 48; e++) {
                            for (int f = e + 1; f <= 49; f++) {

                                long winningMask = toMask(a, b, c, d, e, f);
                                int firstBest = calculateBestMatch(firstMasks, winningMask);
                                int secondBest = calculateBestMatch(secondMasks, winningMask);

                                firstResults[firstBest]++;
                                secondResults[secondBest]++;

                                totalDraws++;
                            }
                        }
                    }
                }
            }
        }

        printResults(firstStrategyName, firstResults, secondStrategyName, secondResults, totalDraws);
    }

    private static long[] convertToMasks(List<List<Integer>> tickets) {
        long[] masks = new long[tickets.size()];

        for (int i = 0; i < tickets.size(); i++) {
            masks[i] = toMask(tickets.get(i));
        }

        return masks;
    }

    private static long toMask(List<Integer> numbers) {
        long mask = 0L;

        for (Integer number : numbers) {
            mask |= 1L << (number - 1);
        }

        return mask;
    }

    private static long toMask(int a, int b, int c, int d, int e, int f) {
        return (1L << (a - 1))
                | (1L << (b - 1))
                | (1L << (c - 1))
                | (1L << (d - 1))
                | (1L << (e - 1))
                | (1L << (f - 1));
    }

    private static int calculateBestMatch(long[] ticketMasks, long winningMask) {
        int bestMatch = 0;

        for (long ticketMask : ticketMasks) {
            int matches = Long.bitCount(ticketMask & winningMask);

            if (matches > bestMatch) {
                bestMatch = matches;
            }

            if (bestMatch == NUMBERS_PER_DRAW) {
                break;
            }
        }

        return bestMatch;
    }

    private static void printResults(
            String firstStrategyName,
            long[] firstResults,
            String secondStrategyName,
            long[] secondResults,
            long totalDraws
    ) {
        System.out.println();
        System.out.println(
                "========== EXACT RESULTS =========="
        );

        System.out.println(
                "Total possible draws: " + totalDraws
        );

        System.out.println();

        System.out.printf("%-10s %24s %24s%n", "Best hit", firstStrategyName, secondStrategyName);

        for (int matches = 0;
             matches <= NUMBERS_PER_DRAW;
             matches++) {

            System.out.printf(
                    Locale.US,
                    "%-10s %10d (%10.6f%%) %10d (%10.6f%%)%n",
                    matches + "/6",
                    firstResults[matches],
                    percentage(
                            firstResults[matches],
                            totalDraws
                    ),
                    secondResults[matches],
                    percentage(
                            secondResults[matches],
                            totalDraws
                    )
            );
        }

        System.out.println();
        System.out.println("At least:");

        for (int minimumMatches = 3;
             minimumMatches <= NUMBERS_PER_DRAW;
             minimumMatches++) {

            long firstAtLeast =
                    countAtLeast(
                            firstResults,
                            minimumMatches
                    );

            long secondAtLeast =
                    countAtLeast(
                            secondResults,
                            minimumMatches
                    );

            System.out.printf(
                    Locale.US,
                    "%d+/6 -> %s: %d (%.6f%%) | %s: %d (%.6f%%)%n",
                    minimumMatches,
                    firstStrategyName,
                    firstAtLeast,
                    percentage(
                            firstAtLeast,
                            totalDraws
                    ),
                    secondStrategyName,
                    secondAtLeast,
                    percentage(
                            secondAtLeast,
                            totalDraws
                    )
            );
        }
    }

    private static long countAtLeast(long[] results, int minimumMatches) {
        long total = 0;

        for (int i = minimumMatches;
             i < results.length;
             i++) {

            total += results[i];
        }

        return total;
    }

    private static double percentage(long count, long total) {
        return count * 100.0 / total;
    }

    private static void validateTickets(List<List<Integer>> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            throw new IllegalArgumentException("tickets must not be null or empty");
        }

        for (List<Integer> ticket : tickets) {
            if (ticket == null || ticket.size() != NUMBERS_PER_DRAW) {
                throw new IllegalArgumentException("Every ticket must contain exactly 6 numbers");
            }

            for (Integer number : ticket) {
                if (number == null || number < 1 || number > MAX_NUMBER) {
                    throw new IllegalArgumentException("Ticket numbers must be between 1 and 49");
                }
            }
        }
    }

    private ExactLottoAnalyzer() {
    }
}