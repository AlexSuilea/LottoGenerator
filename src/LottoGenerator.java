import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LottoGenerator {
    private static final BigDecimal LOTTO649_PRICE = BigDecimal.valueOf(8);
    private static final BigDecimal LOTTO540_PRICE = BigDecimal.valueOf(5);

    public static void main(String[] args) {
        int lotto649TicketCount = 12244;
        int lotto540TicketCount = 0;
        int jokerTicketCount = 0;

        BigDecimal lotto649Cost = LottoMachine.printTickets(
                "Loto 6/49:",
                lotto649TicketCount,
                6,
                49,
                LOTTO649_PRICE,
                3
        );

        BigDecimal lotto540Cost = LottoMachine.printTickets(
                "Loto 5/40:",
                lotto540TicketCount,
                5,
                40,
                LOTTO540_PRICE,
                4
        );

        BigDecimal jokerCost = LottoMachine.printJokerTickets(jokerTicketCount);

        BigDecimal totalCost = lotto649Cost
                .add(lotto540Cost)
                .add(jokerCost);

        System.out.println("Cost total: " + LottoMachine.formatPrice(totalCost));

        Set<List<Integer>> tickets = LottoMachine.generateTickets(12244, 6, 49);

        List<List<Integer>> winners = List.of(
                List.of(12, 13, 36, 37, 44, 45),
                List.of(5, 10, 21, 30, 41, 47),
                List.of(2, 14, 26, 31, 38, 49)
        );

        winners.forEach(winner ->
                System.out.println(
                        winner + " -> " + tickets.contains(winner)
                )
        );

        LottoMachine.testUniformity(
                1_000_000,
                6,
                49
        );
    }
}