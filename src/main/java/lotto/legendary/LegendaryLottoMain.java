package lotto.legendary;

import java.time.LocalDate;

public class LegendaryLottoMain {

    public static void main(String[] args)
            throws Exception {

        var history649 =
                Lotto649HistoryLoader.load();

        var history540 =
                Lotto540HistoryLoader.load(
                        LocalDate.of(2020, 1, 1)
                );

        System.out.println(
                "6/49 history: " + history649.size()
        );

        System.out.println(
                "5/40 history: " + history540.size()
        );

        var legendary649 =
                Legendary649Optimizer.optimize(
                        history649,
                        3,          // 2 bilete legendare
                        250_000
                );

        System.out.println();
        System.out.println("===== LEGENDARY 6/49 =====");

        legendary649.tickets()
                .forEach(System.out::println);

        System.out.println(
                "Historical score: "
                        + legendary649.historicalScore()
        );

        var legendary540 =
                Legendary540Optimizer.optimize(
                        history540,
                        4,
                        250_000
                );

        System.out.println();
        System.out.println("===== LEGENDARY 5/40 =====");

        legendary540.tickets()
                .forEach(System.out::println);

        System.out.println(
                "Historical score: "
                        + legendary540.historicalScore()
        );
    }
}
