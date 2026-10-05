package frequent;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public final class FrequentPrizeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private FrequentPrizeGenerator() {
    }

    public static List<List<Integer>> generateDisjointTickets(
            int ticketCount,
            int numbersPerTicket,
            int maxNumber
    ) {
        if (ticketCount <= 0) {
            throw new IllegalArgumentException(
                    "ticketCount must be greater than 0"
            );
        }

        if (numbersPerTicket <= 0) {
            throw new IllegalArgumentException(
                    "numbersPerTicket must be greater than 0"
            );
        }

        if (ticketCount * numbersPerTicket > maxNumber) {
            throw new IllegalArgumentException(
                    "Not enough numbers to generate fully disjoint tickets"
            );
        }

        List<Integer> pool = new ArrayList<>(
                IntStream.rangeClosed(1, maxNumber)
                        .boxed()
                        .toList()
        );

        Collections.shuffle(pool, RANDOM);

        List<List<Integer>> tickets = new ArrayList<>();

        for (int ticketIndex = 0;
             ticketIndex < ticketCount;
             ticketIndex++) {

            int from =
                    ticketIndex * numbersPerTicket;

            int to =
                    from + numbersPerTicket;

            List<Integer> ticket =
                    pool.subList(from, to)
                            .stream()
                            .sorted()
                            .toList();

            tickets.add(ticket);
        }

        return List.copyOf(tickets);
    }

    /*
     * Exact probability that AT LEAST ONE of the
     * disjoint tickets reaches targetMatches.
     *
     * No Monte Carlo here.
     * This is exact combinatorics.
     */
    public static double calculateHitProbability(
            int ticketCount,
            int ticketSize,
            int maxNumber,
            int drawSize,
            int targetMatches
    ) {
        if (ticketCount * ticketSize > maxNumber) {
            throw new IllegalArgumentException(
                    "Tickets must be disjoint"
            );
        }

        long totalDraws =
                combinations(maxNumber, drawSize);

        /*
         * dp[picked] =
         * number of ways to have 'picked' numbers
         * from the ticket blocks without any
         * individual ticket reaching targetMatches.
         */
        long[] dp = new long[drawSize + 1];
        dp[0] = 1;

        for (int ticket = 0;
             ticket < ticketCount;
             ticket++) {

            long[] next = new long[drawSize + 1];

            for (int alreadyPicked = 0;
                 alreadyPicked <= drawSize;
                 alreadyPicked++) {

                if (dp[alreadyPicked] == 0) {
                    continue;
                }

                int maxAllowedFromTicket =
                        Math.min(
                                targetMatches - 1,
                                Math.min(
                                        ticketSize,
                                        drawSize - alreadyPicked
                                )
                        );

                for (int pickedFromTicket = 0;
                     pickedFromTicket <= maxAllowedFromTicket;
                     pickedFromTicket++) {

                    next[alreadyPicked + pickedFromTicket] +=
                            dp[alreadyPicked]
                                    * combinations(
                                    ticketSize,
                                    pickedFromTicket
                            );
                }
            }

            dp = next;
        }

        int outsideNumbers =
                maxNumber - ticketCount * ticketSize;

        long noHitDraws = 0;

        for (int pickedFromTickets = 0;
             pickedFromTickets <= drawSize;
             pickedFromTickets++) {

            int pickedOutside =
                    drawSize - pickedFromTickets;

            if (pickedOutside < 0
                    || pickedOutside > outsideNumbers) {
                continue;
            }

            noHitDraws +=
                    dp[pickedFromTickets]
                            * combinations(
                            outsideNumbers,
                            pickedOutside
                    );
        }

        return 1.0
                - (double) noHitDraws / totalDraws;
    }

    public static void printResult(
            String game,
            List<List<Integer>> tickets,
            double probability,
            int targetMatches
    ) {
        System.out.println();
        System.out.println(
                "===== FREQUENT " + game + " ====="
        );

        tickets.forEach(System.out::println);

        System.out.println();

        System.out.printf(
                "Probability of at least one %d+ hit: %.4f%%%n",
                targetMatches,
                probability * 100
        );

        System.out.printf(
                "Approx. 1 in %.2f extrageri%n",
                1.0 / probability
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
