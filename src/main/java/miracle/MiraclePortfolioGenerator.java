package miracle;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public final class MiraclePortfolioGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int MAX_OVERLAP = 1;
    private static final int MAX_ATTEMPTS = 100_000;

    private MiraclePortfolioGenerator() {
    }

    public static List<List<Integer>> generate(
            int ticketCount,
            int ticketSize,
            int maxNumber
    ) {
        if (ticketCount <= 0) {
            throw new IllegalArgumentException(
                    "ticketCount must be greater than 0"
            );
        }

        if (ticketSize <= 0 || ticketSize > maxNumber) {
            throw new IllegalArgumentException(
                    "Invalid ticketSize"
            );
        }

        List<List<Integer>> tickets = new ArrayList<>();

        for (int i = 0; i < ticketCount; i++) {

            boolean added = false;

            for (int attempt = 0;
                 attempt < MAX_ATTEMPTS;
                 attempt++) {

                List<Integer> candidate =
                        generateTicket(
                                ticketSize,
                                maxNumber
                        );

                boolean valid =
                        tickets.stream()
                                .allMatch(existing ->
                                        overlap(
                                                existing,
                                                candidate
                                        ) <= MAX_OVERLAP
                                );

                if (valid && !tickets.contains(candidate)) {
                    tickets.add(candidate);
                    added = true;
                    break;
                }
            }

            if (!added) {
                throw new IllegalStateException(
                        "Could not generate requested portfolio"
                );
            }
        }

        return List.copyOf(tickets);
    }

    private static List<Integer> generateTicket(
            int ticketSize,
            int maxNumber
    ) {
        List<Integer> pool = new ArrayList<>(
                IntStream.rangeClosed(1, maxNumber)
                        .boxed()
                        .toList()
        );

        Collections.shuffle(pool, RANDOM);

        return pool.stream()
                .limit(ticketSize)
                .sorted()
                .toList();
    }

    private static int overlap(
            List<Integer> first,
            List<Integer> second
    ) {
        int matches = 0;

        for (int number : first) {
            if (second.contains(number)) {
                matches++;
            }
        }

        return matches;
    }

    public static void printExactStats(
            String game,
            List<List<Integer>> tickets,
            int maxNumber,
            int ticketSize,
            int drawSize,
            int... targets
    ) {
        System.out.println();
        System.out.println(
                "===== MIRACLE " + game + " ====="
        );

        tickets.forEach(System.out::println);

        System.out.println();

        printOverlaps(tickets);

        long totalDraws =
                combinations(
                        maxNumber,
                        drawSize
                );

        for (int target : targets) {

            long coveredPerTicket =
                    coveragePerTicket(
                            maxNumber,
                            ticketSize,
                            drawSize,
                            target
                    );

            /*
             * Because pairwise overlap <= 1 and
             * target >= 4 with a 6-number draw,
             * two tickets cannot simultaneously
             * reach the target.
             *
             * Therefore coverage is additive and exact.
             */
            long coveredDraws =
                    coveredPerTicket * tickets.size();

            double probability =
                    (double) coveredDraws / totalDraws;

            System.out.printf(
                    "%d+ matches:%n",
                    target
            );

            System.out.printf(
                    "Covered draws: %,d / %,d%n",
                    coveredDraws,
                    totalDraws
            );

            System.out.printf(
                    "Probability: %.8f%%%n",
                    probability * 100
            );

            System.out.printf(
                    "Approx. 1 in %,.2f extrageri%n%n",
                    1.0 / probability
            );
        }
    }

    private static long coveragePerTicket(
            int maxNumber,
            int ticketSize,
            int drawSize,
            int target
    ) {
        long coverage = 0;

        int maximumMatches =
                Math.min(
                        ticketSize,
                        drawSize
                );

        for (int matches = target;
             matches <= maximumMatches;
             matches++) {

            coverage +=
                    combinations(
                            ticketSize,
                            matches
                    )
                            *
                            combinations(
                                    maxNumber - ticketSize,
                                    drawSize - matches
                            );
        }

        return coverage;
    }

    private static void printOverlaps(
            List<List<Integer>> tickets
    ) {
        int maxObservedOverlap = 0;

        for (int i = 0; i < tickets.size(); i++) {

            for (int j = i + 1;
                 j < tickets.size();
                 j++) {

                int overlap =
                        overlap(
                                tickets.get(i),
                                tickets.get(j)
                        );

                maxObservedOverlap =
                        Math.max(
                                maxObservedOverlap,
                                overlap
                        );
            }
        }

        System.out.println(
                "Maximum pairwise overlap: "
                        + maxObservedOverlap
        );
    }

    private static long combinations(
            int n,
            int k
    ) {
        if (k < 0 || k > n) {
            return 0;
        }

        if (k == 0 || k == n) {
            return 1;
        }

        k = Math.min(k, n - k);

        long result = 1;

        for (int i = 1; i <= k; i++) {
            result =
                    result
                            * (n - k + i)
                            / i;
        }

        return result;
    }
}