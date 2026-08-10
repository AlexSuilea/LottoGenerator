import java.util.ArrayList;
import java.util.List;

public class SmartTicketGenerator {
    public static final int LOTTO_649_NUMBERS = 6;
    public static final int LOTTO_649_MAX_NUMBER = 49;

    public static List<List<Integer>> generateDiversifiedTickets(int ticketCount, int candidatesPerTicket) {
        if(ticketCount <= 0) {
            throw new IllegalArgumentException("ticketCount must be greater than 0");
        }

        if(candidatesPerTicket <= 0) {
            throw new IllegalArgumentException("ticketCount must be greater than 0");
        }

        List<List<Integer>> selectedTickets = new ArrayList<>();

        while(selectedTickets.size() < ticketCount) {
            List<Integer> bestCandidate = null;
            double bestScore = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < candidatesPerTicket; i++) {
                List<Integer> candidate = LottoMachine.generateTicket(LOTTO_649_NUMBERS, LOTTO_649_MAX_NUMBER);

                if(selectedTickets.contains(candidate)) {
                    continue;
                }

                double score = calculateBalancedScore(candidate) + calculateDiversityScore(candidate, selectedTickets);

                if(score > bestScore) {
                    bestScore = score;
                    bestCandidate = candidate;
                }
            }

            if(bestCandidate == null) {
                throw new IllegalArgumentException("Could not generate a unique ticket");
            }

            selectedTickets.add(bestCandidate);
        }

        return selectedTickets;
    }

    private static double calculateBalancedScore(List<Integer> ticket) {
        return sumScore(ticket) + oddEvenScore(ticket) + lowHighScore(ticket) + spreadScore(ticket)
                + consecutiveScore(ticket);
    }

    private static double sumScore(List<Integer> ticket) {
        int sum = ticket.stream()
                .mapToInt(Integer::intValue)
                .sum();

        int theoreticalAverageSum = 150;

        return Math.max(0, 20.0 - Math.abs(sum - theoreticalAverageSum) * 0.15);
    }

    private static double oddEvenScore(List<Integer> ticket) {
        long oddCount = ticket.stream()
                .filter(number -> number % 2 != 0)
                .count();

        return switch((int) oddCount) {
            case 3 -> 20;
            case 2, 4 -> 15;
            case 1, 5 -> 5;
            default -> 0;
        };
    }

    private static double lowHighScore(List<Integer> ticket) {
        long lowCount = ticket.stream()
                .filter(number -> number <= 24)
                .count();

        return switch ((int) lowCount) {
            case 3 -> 20;
            case 2, 4 -> 15;
            case 1, 5 -> 5;
            default -> 0;
        };
    }

    private static double spreadScore(List<Integer> ticket) {
        int spread = ticket.getLast() - ticket.getFirst();

        if(spread >= 35) return 20;
        if(spread >= 25) return 15;
        if(spread >= 15) return 10;

        return 0;
    }

    private static double consecutiveScore(List<Integer> ticket) {
        int consecutivePairs = 0;
        for (int i = 1; i < ticket.size(); i++) {
            if(ticket.get(i) - ticket.get(i-1) == 1) {
                consecutivePairs++;
            }
        }

        return switch (consecutivePairs) {
            case 0 -> 15;
            case 1 -> 10;
            case 2 -> 5;
            default -> 0;
        };
    }

    private static double calculateDiversityScore(List<Integer> candidate, List<List<Integer>> existingTickets) {
        if(existingTickets.isEmpty()) return 30;

        int totalOverlap = 0;
        int maximumOverlap = 0;

        for (List<Integer> existingTicket : existingTickets) {
            int overlap = calculateOverlap(candidate, existingTicket);

            totalOverlap += overlap;
            maximumOverlap = Math.max(maximumOverlap, overlap);
        }

        double score = 30;

        score -= totalOverlap * 2.5;
        score -= maximumOverlap * 5.0;

        return score;
    }

    private static int calculateOverlap(List<Integer> candidate, List<Integer> existingTicket) {
        int overlap = 0;
        for (Integer number : candidate) {
            if(existingTicket.contains(number)) overlap++;
        }
        return overlap;
    }

    private SmartTicketGenerator() {}
}
