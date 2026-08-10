import java.util.List;

public class LottoGenerator {
    private static final int DEFAULT_CANDIDATES_PER_TICKET = 1000;

    public static void main(String[] args) {
        List<List<Integer>> tickets =
                CombinatorialSmartTicketGenerator.generateTickets(
                        12,
                        DEFAULT_CANDIDATES_PER_TICKET
                );

        tickets.forEach(System.out::println);

        TicketAnalyzer.printAnalysis(
                "COMBINATORIAL",
                tickets,
                49
        );
    }
}