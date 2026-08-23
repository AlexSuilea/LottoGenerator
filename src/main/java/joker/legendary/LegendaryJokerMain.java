package joker.legendary;

import java.time.LocalDate;
import java.util.List;

public class LegendaryJokerMain {

    private static final int OPTIMIZER_SIMULATIONS =
            100_000;

    private static final int RANDOM_BASELINE_SIMULATIONS =
            20_000;

    public static void main(String[] args)
            throws Exception {

        List<JokerDraw> history =
                JokerHistoryLoader.load(
                        LocalDate.of(2022, 1, 1),
                        LocalDate.now()
                );

        System.out.println(
                "Loaded draws: "
                        + history.size()
        );

        LocalDate validationStart =
                LocalDate.of(2026, 1, 1);

        List<JokerDraw> training =
                history.stream()
                        .filter(draw ->
                                draw.date()
                                        .isBefore(
                                                validationStart
                                        )
                        )
                        .toList();

        List<JokerDraw> validation =
                history.stream()
                        .filter(draw ->
                                !draw.date()
                                        .isBefore(
                                                validationStart
                                        )
                        )
                        .toList();

        System.out.println(
                "Training draws: "
                        + training.size()
        );

        System.out.println(
                "Validation draws: "
                        + validation.size()
        );

        System.out.println();
        System.out.println(
                "===== TRAINING 2022-2025 ====="
        );

        var optimized =
                LegendaryJokerOptimizer.optimize(
                        training,
                        OPTIMIZER_SIMULATIONS
                );

        printPair(
                optimized.pair()
        );

        System.out.println(
                "Training score: "
                        + optimized
                        .evaluation()
                        .score()
        );

        printHits(
                optimized.evaluation()
        );

        System.out.println();
        System.out.println(
                "===== VALIDATION 2026 ====="
        );

        var validationResult =
                LegendaryJokerOptimizer.evaluate(
                        optimized.pair(),
                        validation
                );

        System.out.println(
                "Validation score: "
                        + validationResult.score()
        );

        printHits(validationResult);

        double percentile =
                LegendaryJokerOptimizer
                        .calculateRandomPercentile(
                                validationResult.score(),
                                validation,
                                RANDOM_BASELINE_SIMULATIONS
                        );

        System.out.printf(
                "Random baseline percentile: %.2f%%%n",
                percentile
        );

        System.out.println();
        System.out.println(
                "===== FINAL LEGENDARY PAIR ====="
        );

        /*
         * Acum că experimentul s-a terminat,
         * refacem optimizerul pe TOT istoricul:
         * 2022 -> prezent.
         */
        var finalResult =
                LegendaryJokerOptimizer.optimize(
                        history,
                        OPTIMIZER_SIMULATIONS
                );

        printPair(
                finalResult.pair()
        );

        System.out.println(
                "Historical score: "
                        + finalResult
                        .evaluation()
                        .score()
        );
    }

    private static void printPair(
            LegendaryJokerOptimizer
                    .TicketPair pair
    ) {

        System.out.println(
                "Ticket A: "
                        + pair.first()
        );

        System.out.println(
                "Ticket B: "
                        + pair.second()
        );
    }

    private static void printHits(
            LegendaryJokerOptimizer
                    .Evaluation evaluation
    ) {

        evaluation.hits()
                .forEach(
                        (category, count) -> {

                            if (count > 0) {

                                System.out.printf(
                                        "%-15s -> %d%n",
                                        category.label(),
                                        count
                                );
                            }
                        }
                );
    }
}
