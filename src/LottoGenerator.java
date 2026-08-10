import java.math.BigDecimal;
import java.util.List;

public class LottoGenerator  {
    private static final BigDecimal LOTTO649_PRICE = BigDecimal.valueOf(8);
    private static final BigDecimal LOTTO540_PRICE = BigDecimal.valueOf(5);
    public static void main(String[] args) {
//        int lotto649TicketCount = 2;
//        int lotto540TicketCount = 1;
//        int jokerTicketCount = 1;
//
//        BigDecimal lotto649Cost = LottoMachine.printTickets(
//                "Loto 6/49:",
//                lotto649TicketCount,
//                6,
//                49,
//                LOTTO649_PRICE,
//                3
//        );
//
//        BigDecimal lotto540Cost = LottoMachine.printTickets(
//                "Loto 5/40:",
//                lotto540TicketCount,
//                5,
//                40,
//                LOTTO540_PRICE,
//                4
//        );
//
//        BigDecimal jokerCost = LottoMachine.printJokerTickets(jokerTicketCount);
//
//        BigDecimal totalCost = lotto649Cost
//                .add(lotto540Cost)
//                .add(jokerCost);
//
//        System.out.println("Cost total: " + LottoMachine.formatPrice(totalCost));
//
//        System.out.println();
//        System.out.println("Smart diversified Loto 6/49:");
//
//        List<List<Integer>> smartTickets = SmartTicketGenerator.generateDiversifiedTickets(10,
//                10000);
//
//        smartTickets.forEach(System.out::println);

        System.out.println();
        System.out.println("COMPARISON RANDOM vs SMART");
        System.out.println();

        int comparisonTicketCount = 10;

        List<List<Integer>> randomTickets =
                new java.util.ArrayList<>();

        while (randomTickets.size() < comparisonTicketCount) {

            List<Integer> ticket =
                    LottoMachine.generateTicket(6, 49);

            if (!randomTickets.contains(ticket)) {
                randomTickets.add(ticket);
            }
        }

        List<List<Integer>> smartTickets =
                SmartTicketGenerator.generateDiversifiedTickets(
                        comparisonTicketCount,
                        10_000
                );

        System.out.println("Random tickets:");
        randomTickets.forEach(System.out::println);

        System.out.println();

        System.out.println("Smart tickets:");
        smartTickets.forEach(System.out::println);

        TicketAnalyzer.printAnalysis(
                "RANDOM",
                randomTickets,
                49
        );

        TicketAnalyzer.printAnalysis(
                "SMART",
                smartTickets,
                49
        );
    }
}