package lotto.legendary;

import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

public final class Legendary649Optimizer {

    private static final int MAX_NUMBER = 49;
    private static final int NUMBERS_PER_TICKET = 6;
    private static final int MAX_OVERLAP = 2;

    private Legendary649Optimizer() {}

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
            Set<Integer> winning =
                    new HashSet<>(draw.numbers());

            for (List<Integer> ticket : tickets) {
                int matches = matches(ticket, winning);

                score += switch (matches) {
                    case 6 -> 10_000;
                    case 5 -> 500;
                    case 4 -> 30;
                    case 3 -> 1;
                    default -> 0;
                };
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
            List<Integer> candidate = generateTicket(random);

            boolean acceptable = result.stream()
                    .allMatch(existing ->
                            overlap(existing, candidate) <= MAX_OVERLAP
                    );

            if (acceptable) {
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
