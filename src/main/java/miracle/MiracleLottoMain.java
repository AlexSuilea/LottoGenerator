package miracle;

import java.util.List;

public class MiracleLottoMain {

    public static void main(String[] args) {

        int ticketCount = 2;

        // ==============================
        // LOTO 6/49
        // ==============================

        List<List<Integer>> lotto649 =
                MiraclePortfolioGenerator.generate(
                        ticketCount,
                        6,
                        49
                );

        MiraclePortfolioGenerator.printExactStats(
                "6/49",
                lotto649,
                49,
                6,
                6,
                4,
                5,
                6
        );


        // ==============================
        // LOTO 5/40
        // ==============================

        List<List<Integer>> lotto540 =
                MiraclePortfolioGenerator.generate(
                        ticketCount,
                        5,
                        40
                );

        MiraclePortfolioGenerator.printExactStats(
                "5/40",
                lotto540,
                40,
                5,
                6,
                4,
                5
        );
    }
}
