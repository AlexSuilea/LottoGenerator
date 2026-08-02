import java.math.BigDecimal;

public class LottoGenerator  {
    private static final BigDecimal LOTTO649_PRICE = BigDecimal.valueOf(8);
    private static final BigDecimal LOTTO540_PRICE = BigDecimal.valueOf(5);
    public static void main(String[] args) {
        int lotto649TicketCount = 1;
        int lotto540TicketCount = 1;
        int jokerTicketCount = 1;

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
    }


}