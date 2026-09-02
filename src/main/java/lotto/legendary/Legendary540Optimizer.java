package lotto.legendary;

import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

public final class Legendary540Optimizer {

    private static final int MAX_NUMBER = 40;
    private static final int NUMBERS_PER_TICKET = 5;
    private static final int MAX_OVERLAP = 1;

    private Legendary540Optimizer() {}

    public static Result optimize(
            List<LottoDraw> history,
            int ticketCount,
            int simulations,
            long seed
    ) {
        Random random = new Random(seed);

        List<List<Integer>> best = null;
        long bestScore = Long.MIN_VALUE;

        for (int i = 0; i < simulations; i++) {
            List<List<Integer>> candidate =
                    generateTickets(ticketCount, random);

            long score = score(candidate, history);

            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        return new Result(best, bestScore);
    }

    public static long score(
            List<List<Integer>> tickets,
            List<LottoDraw> history
    ) {
        long score = 0;

        for (LottoDraw draw : history) {

            Set<Integer> firstFive =
                    new HashSet<>(draw.numbers().subList(0, 5));

            Set<Integer> allSix =
                    new HashSet<>(draw.numbers());

            for (List<Integer> ticket : tickets) {

                int firstFiveMatches =
                        matches(ticket, firstFive);

                int allSixMatches =
                        matches(ticket, allSix);

                if (firstFiveMatches == 5) {
                    score += 10_000;  // Cat I
                } else if (allSixMatches == 5) {
                    score += 1_000;   // Cat II
                } else if (allSixMatches == 4) {
                    score += 30;      // Cat III
                }
            }
        }

        return score;
    }

    private static List<List<Integer>> generateTickets(
            int count,
            RandomGenerator random
    ) {
        List<List<Integer>> result = new ArrayList<>();

        while (result.size() < count) {
            List<Integer> candidate =
                    generateTicket(random);

            if (result.stream().allMatch(existing ->
                    overlap(existing, candidate) <= MAX_OVERLAP)) {
                result.add(candidate);
            }
        }

        return result;
    }

    private static List<Integer> generateTicket(
            RandomGenerator random
    ) {
        List<Integer> pool = new ArrayList<>(
                IntStream.rangeClosed(1, MAX_NUMBER)
                        .boxed()
                        .toList()
        );

        Collections.shuffle(pool, random);

        return pool.stream()
                .limit(NUMBERS_PER_TICKET)
                .sorted()
                .toList();
    }

    private static int matches(
            List<Integer> ticket,
            Set<Integer> winning
    ) {
        return (int) ticket.stream()
                .filter(winning::contains)
                .count();
    }

    private static int overlap(
            List<Integer> a,
            List<Integer> b
    ) {
        return (int) a.stream()
                .filter(b::contains)
                .count();
    }

    public record Result(
            List<List<Integer>> tickets,
            long historicalScore
    ) {}
}
