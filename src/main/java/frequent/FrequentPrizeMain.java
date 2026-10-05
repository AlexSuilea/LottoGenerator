package frequent;

import java.util.List;

public class FrequentPrizeMain {

    public static void main(String[] args) {

        int lotto649TicketCount = 4;
        int lotto540TicketCount = 4;

        // ===== LOTO 6/49 =====

        List<List<Integer>> lotto649Tickets =
                FrequentPrizeGenerator
                        .generateDisjointTickets(
                                lotto649TicketCount,
                                6,
                                49
                        );

        double lotto649Probability =
                FrequentPrizeGenerator
                        .calculateHitProbability(
                                lotto649TicketCount,
                                6,
                                49,
                                6,
                                3
                        );

        FrequentPrizeGenerator.printResult(
                "6/49",
                lotto649Tickets,
                lotto649Probability,
                3
        );


        // ===== LOTO 5/40 =====

        List<List<Integer>> lotto540Tickets =
                FrequentPrizeGenerator
                        .generateDisjointTickets(
                                lotto540TicketCount,
                                5,
                                40
                        );

        double lotto540Probability =
                FrequentPrizeGenerator
                        .calculateHitProbability(
                                lotto540TicketCount,
                                5,
                                40,
                                6,
                                4
                        );

        FrequentPrizeGenerator.printResult(
                "5/40",
                lotto540Tickets,
                lotto540Probability,
                4
        );
    }
}
