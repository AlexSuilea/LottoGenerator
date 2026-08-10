import java.util.List;

public class LottoGenerator {

    public static void main(String[] args) {

        int ticketCount = 10;
        int candidatesPerTicket = 10_000;

        List<List<Integer>> combinatorialTickets =
                CombinatorialSmartTicketGenerator.generateTickets(
                        ticketCount,
                        candidatesPerTicket
                );

        System.out.println("BEFORE PORTFOLIO OPTIMIZATION:");
        combinatorialTickets.forEach(System.out::println);

        PortfolioOptimizer.OptimizationResult optimizationResult =
                PortfolioOptimizer.optimize(
                        combinatorialTickets,
                        25_000,
                        5_000,
                        20260810L
                );

        List<List<Integer>> optimizedTickets =
                optimizationResult.tickets();

        System.out.println();
        System.out.println("3+ PORTFOLIO OPTIMIZED:");
        optimizedTickets.forEach(System.out::println);

        System.out.printf(
                "Training sample 3+ coverage: %.4f%%%n",
                optimizationResult.coveragePercentage()
        );

        System.out.println(
                "Distinct numbers: "
                        + optimizationResult.distinctNumbers()
        );

        System.out.println(
                "Accepted mutations: "
                        + optimizationResult.acceptedMutations()
        );

        TicketAnalyzer.printAnalysis(
                "3+ PORTFOLIO OPTIMIZED",
                optimizedTickets,
                49
        );

        ExactLottoAnalyzer.compare(
                "COMBINATORIAL",
                combinatorialTickets,
                "OPTIMIZED",
                optimizedTickets
        );
    }
}