import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TicketAnalyzer {
    public static void printAnalysis(String name, List<List<Integer>> tickets, int maxNumber) {
        int distinctNumbers = countDistinctNumbers(tickets);
        double coveragePercentage = distinctNumbers * 100.0 / maxNumber;
        double averageOverlap = calculateAverageOverlap(tickets);
        int maximumOverlap = calculateMaximumOverlap(tickets);
        Map<Integer, Integer> frequencies = calculateFrequencies(tickets);

        System.out.println();
        System.out.println("===== " + name + " =====");

        System.out.println(
                "Tickets: " + tickets.size()
        );

        System.out.println(
                "Distinct numbers: "
                        + distinctNumbers
                        + "/"
                        + maxNumber
        );

        System.out.printf(
                "Coverage: %.2f%%%n",
                coveragePercentage
        );

        System.out.printf(
                "Average overlap: %.2f%n",
                averageOverlap
        );

        System.out.println(
                "Maximum overlap: "
                        + maximumOverlap
        );

        System.out.println(
                "Number frequencies:"
        );

        frequencies.forEach(
                (number, frequency) ->
                        System.out.println(
                                number + " -> " + frequency
                        )
        );

        System.out.println();
    }

    private static int countDistinctNumbers(List<List<Integer>> tickets) {
        return (int) tickets.stream()
                .flatMap(List::stream)
                .distinct()
                .count();
    }

    private static Map<Integer, Integer> calculateFrequencies(List<List<Integer>> tickets) {
        Map<Integer, Integer> frequencies = new TreeMap<>();

        for (List<Integer> ticket : tickets) {
            for (Integer number : ticket) {
                frequencies.merge(number, 1, Integer::sum);
            }
        }

        return frequencies;
    }

    private static double calculateAverageOverlap(List<List<Integer>> tickets) {
        if (tickets.size() < 2) {
            return 0;
        }

        int totalOverlap = 0;
        int comparisons = 0;

        for (int i = 0; i < tickets.size(); i++) {
            for (int j = i + 1; j < tickets.size(); j++) {
                totalOverlap += SmartTicketGenerator.calculateOverlap(tickets.get(i), tickets.get(j));
                comparisons++;
            }
        }

        return (double) totalOverlap / comparisons;
    }

    private static int calculateMaximumOverlap(List<List<Integer>> tickets) {
        int maximumOverlap = 0;

        for (int i = 0; i < tickets.size(); i++) {
            for (int j = i + 1; j < tickets.size(); j++) {
                int overlap = SmartTicketGenerator.calculateOverlap(tickets.get(i), tickets.get(j));
                maximumOverlap = Math.max(maximumOverlap, overlap);
            }
        }

        return maximumOverlap;
    }

    private TicketAnalyzer() {}
}
