package joker.legendary;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class LegendaryJokerOptimizer {

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private static final int MAX_OVERLAP = 1;

    private LegendaryJokerOptimizer() {
    }

    public static OptimizationResult optimize(
            List<JokerDraw> history,
            int simulations
    ) {

        if (history == null || history.isEmpty()) {
            throw new IllegalArgumentException(
                    "History must not be empty"
            );
        }

        if (simulations <= 0) {
            throw new IllegalArgumentException(
                    "simulations must be greater than 0"
            );
        }

        TicketPair bestPair = null;
        Evaluation bestEvaluation = null;

        for (int i = 0; i < simulations; i++) {

            TicketPair pair =
                    generateRandomPair();

            Evaluation evaluation =
                    evaluate(pair, history);

            if (bestEvaluation == null
                    || evaluation.score()
                    > bestEvaluation.score()) {

                bestPair = pair;
                bestEvaluation = evaluation;
            }
        }

        return new OptimizationResult(
                bestPair,
                bestEvaluation
        );
    }

    public static Evaluation evaluate(
            TicketPair pair,
            List<JokerDraw> history
    ) {

        long totalScore = 0;

        EnumMap<PrizeCategory, Integer> hits =
                new EnumMap<>(PrizeCategory.class);

        for (PrizeCategory category
                : PrizeCategory.values()) {

            if (category != PrizeCategory.NONE) {
                hits.put(category, 0);
            }
        }

        for (JokerDraw draw : history) {

            PrizeCategory firstCategory =
                    classify(pair.first(), draw);

            PrizeCategory secondCategory =
                    classify(pair.second(), draw);

            totalScore +=
                    firstCategory.weight();

            totalScore +=
                    secondCategory.weight();

            if (firstCategory != PrizeCategory.NONE) {
                hits.merge(
                        firstCategory,
                        1,
                        Integer::sum
                );
            }

            if (secondCategory != PrizeCategory.NONE) {
                hits.merge(
                        secondCategory,
                        1,
                        Integer::sum
                );
            }
        }

        return new Evaluation(
                totalScore,
                Map.copyOf(hits)
        );
    }

    public static double calculateRandomPercentile(
            long targetScore,
            List<JokerDraw> validationHistory,
            int simulations
    ) {

        int worseOrEqual = 0;

        for (int i = 0; i < simulations; i++) {

            TicketPair randomPair =
                    generateRandomPair();

            long score =
                    evaluate(
                            randomPair,
                            validationHistory
                    ).score();

            if (score <= targetScore) {
                worseOrEqual++;
            }
        }

        return worseOrEqual
                * 100.0
                / simulations;
    }

    private static TicketPair generateRandomPair() {

        JokerTicket first =
                generateTicket();

        JokerTicket second;

        do {
            second = generateTicket();
        } while (
                first.joker() == second.joker()
                        || overlap(
                        first.numbers(),
                        second.numbers()
                ) > MAX_OVERLAP
        );

        return new TicketPair(
                first,
                second
        );
    }

    private static JokerTicket generateTicket() {

        List<Integer> pool = new ArrayList<>(
                IntStream.rangeClosed(1, 45)
                        .boxed()
                        .toList()
        );

        Collections.shuffle(pool, RANDOM);

        List<Integer> numbers =
                pool.stream()
                        .limit(5)
                        .sorted()
                        .toList();

        int joker =
                RANDOM.nextInt(1, 21);

        return new JokerTicket(
                numbers,
                joker
        );
    }

    private static PrizeCategory classify(
            JokerTicket ticket,
            JokerDraw draw
    ) {

        int mainMatches =
                overlap(
                        ticket.numbers(),
                        draw.numbers()
                );

        boolean jokerMatch =
                ticket.joker() == draw.joker();

        return switch (mainMatches) {

            case 5 ->
                    jokerMatch
                            ? PrizeCategory.CAT_I
                            : PrizeCategory.CAT_II;

            case 4 ->
                    jokerMatch
                            ? PrizeCategory.CAT_III
                            : PrizeCategory.CAT_IV;

            case 3 ->
                    jokerMatch
                            ? PrizeCategory.CAT_V
                            : PrizeCategory.CAT_VI;

            case 2 ->
                    jokerMatch
                            ? PrizeCategory.CAT_VII
                            : PrizeCategory.NONE;

            case 1 ->
                    jokerMatch
                            ? PrizeCategory.CAT_VIII
                            : PrizeCategory.NONE;

            default ->
                    PrizeCategory.NONE;
        };
    }

    private static int overlap(
            List<Integer> first,
            List<Integer> second
    ) {

        int matches = 0;

        for (Integer number : first) {

            if (second.contains(number)) {
                matches++;
            }
        }

        return matches;
    }

    public enum PrizeCategory {

        CAT_I("5/5 + Joker", 300),
        CAT_II("5/5", 200),

        CAT_III("4/5 + Joker", 150),
        CAT_IV("4/5", 100),

        CAT_V("3/5 + Joker", 20),
        CAT_VI("3/5", 10),

        CAT_VII("2/5 + Joker", 3),
        CAT_VIII("1/5 + Joker", 1),

        NONE("No prize", 0);

        private final String label;
        private final int weight;

        PrizeCategory(
                String label,
                int weight
        ) {
            this.label = label;
            this.weight = weight;
        }

        public String label() {
            return label;
        }

        public int weight() {
            return weight;
        }
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

    public record TicketPair(
            JokerTicket first,
            JokerTicket second
    ) {
    }

    public record Evaluation(
            long score,
            Map<PrizeCategory, Integer> hits
    ) {
    }

    public record OptimizationResult(
            TicketPair pair,
            Evaluation evaluation
    ) {
    }
}