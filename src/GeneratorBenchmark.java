import java.util.List;
import java.util.Locale;

public final class GeneratorBenchmark {

    private static final int MAX_NUMBER = 49;

    public static void run(int ticketCount) {

        BenchmarkConfig[] configs = {
                new BenchmarkConfig(1_000, 30),
                new BenchmarkConfig(5_000, 30),
                new BenchmarkConfig(10_000, 30),
                new BenchmarkConfig(25_000, 20),
                new BenchmarkConfig(50_000, 15),
                new BenchmarkConfig(100_000, 10)
        };

        System.out.println();
        System.out.println("========== GENERATOR BENCHMARK ==========");
        System.out.println("Tickets per portfolio: " + ticketCount);
        System.out.println();

        System.out.printf(
                "%-12s %-8s %-14s %-14s %-18s %-18s%n",
                "Candidates",
                "Runs",
                "Avg time ms",
                "Avg coverage",
                "49/49 success",
                "Overlap <= 1"
        );

        for (BenchmarkConfig config : configs) {

            // Warm-up pentru JIT
            warmUp(ticketCount, config.candidatesPerTicket());

            BenchmarkResult result =
                    benchmark(
                            ticketCount,
                            config.candidatesPerTicket(),
                            config.runs()
                    );

            System.out.printf(
                    Locale.US,
                    "%-12d %-8d %-14.2f %-13.2f%% %-17.2f%% %-17.2f%%%n",
                    config.candidatesPerTicket(),
                    config.runs(),
                    result.averageTimeMs(),
                    result.averageCoverage(),
                    result.fullCoverageRate(),
                    result.validOverlapRate()
            );
        }

        System.out.println();
    }

    private static BenchmarkResult benchmark(
            int ticketCount,
            int candidatesPerTicket,
            int runs
    ) {
        long totalTimeNs = 0;

        double totalCoverage = 0;
        double totalAverageOverlap = 0;

        int fullCoverageRuns = 0;
        int validOverlapRuns = 0;

        for (int run = 0; run < runs; run++) {

            long start = System.nanoTime();

            List<List<Integer>> tickets =
                    CombinatorialSmartTicketGenerator.generateTickets(
                            ticketCount,
                            candidatesPerTicket
                    );

            long end = System.nanoTime();

            totalTimeNs += end - start;

            PortfolioMetrics metrics =
                    analyze(tickets);

            totalCoverage +=
                    metrics.coveragePercentage();

            totalAverageOverlap +=
                    metrics.averageOverlap();

            if (metrics.distinctNumbers() == MAX_NUMBER) {
                fullCoverageRuns++;
            }

            if (metrics.maximumOverlap() <= 1) {
                validOverlapRuns++;
            }
        }

        double averageTimeMs =
                totalTimeNs
                        / 1_000_000.0
                        / runs;

        double averageCoverage =
                totalCoverage / runs;

        double averageOverlap =
                totalAverageOverlap / runs;

        double fullCoverageRate =
                fullCoverageRuns
                        * 100.0
                        / runs;

        double validOverlapRate =
                validOverlapRuns
                        * 100.0
                        / runs;

        return new BenchmarkResult(
                averageTimeMs,
                averageCoverage,
                averageOverlap,
                fullCoverageRate,
                validOverlapRate
        );
    }

    private static PortfolioMetrics analyze(
            List<List<Integer>> tickets
    ) {
        boolean[] numbers =
                new boolean[MAX_NUMBER + 1];

        for (List<Integer> ticket : tickets) {
            for (Integer number : ticket) {
                numbers[number] = true;
            }
        }

        int distinctNumbers = 0;

        for (int number = 1;
             number <= MAX_NUMBER;
             number++) {

            if (numbers[number]) {
                distinctNumbers++;
            }
        }

        int totalOverlap = 0;
        int comparisons = 0;
        int maximumOverlap = 0;

        for (int i = 0; i < tickets.size(); i++) {

            for (int j = i + 1;
                 j < tickets.size();
                 j++) {

                int overlap =
                        calculateOverlap(
                                tickets.get(i),
                                tickets.get(j)
                        );

                totalOverlap += overlap;
                comparisons++;

                maximumOverlap =
                        Math.max(
                                maximumOverlap,
                                overlap
                        );
            }
        }

        double averageOverlap =
                comparisons == 0
                        ? 0
                        : (double) totalOverlap
                        / comparisons;

        double coveragePercentage =
                distinctNumbers
                        * 100.0
                        / MAX_NUMBER;

        return new PortfolioMetrics(
                distinctNumbers,
                coveragePercentage,
                averageOverlap,
                maximumOverlap
        );
    }

    private static int calculateOverlap(
            List<Integer> first,
            List<Integer> second
    ) {
        int overlap = 0;

        for (Integer number : first) {
            if (second.contains(number)) {
                overlap++;
            }
        }

        return overlap;
    }

    private static void warmUp(
            int ticketCount,
            int candidatesPerTicket
    ) {
        int warmupCandidates =
                Math.min(
                        candidatesPerTicket,
                        5_000
                );

        for (int i = 0; i < 3; i++) {
            CombinatorialSmartTicketGenerator.generateTickets(
                    ticketCount,
                    warmupCandidates
            );
        }
    }

    private record BenchmarkConfig(
            int candidatesPerTicket,
            int runs
    ) {
    }

    private record PortfolioMetrics(
            int distinctNumbers,
            double coveragePercentage,
            double averageOverlap,
            int maximumOverlap
    ) {
    }

    private record BenchmarkResult(
            double averageTimeMs,
            double averageCoverage,
            double averageOverlap,
            double fullCoverageRate,
            double validOverlapRate
    ) {
    }

    private GeneratorBenchmark() {
    }
}