import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CombinatorialSmartTicketGenerator {

    private static final int NUMBERS_PER_TICKET = 6;
    private static final int MAX_NUMBER = 49;

    /*
     * Weight-urile sunt euristice.
     *
     * Vrem:
     * - coverage bun de numere
     * - cât mai multe triple noi
     * - cât mai multe cvadruple noi
     * - cât mai multe combinații de 5 noi
     * - overlap mic între bilete
     */
    private static final double NEW_NUMBER_WEIGHT = 2.0;
    private static final double TRIPLE_WEIGHT = 1.0;
    private static final double QUADRUPLE_WEIGHT = 2.0;
    private static final double FIVE_NUMBER_WEIGHT = 4.0;

    private static final double OVERLAP_PENALTY = 15.0;

    public static List<List<Integer>> generateTickets(int ticketCount, int candidatesPerTicket) {
        if (ticketCount <= 0) {
            throw new IllegalArgumentException("ticketCount must be greater than 0");
        }

        if (candidatesPerTicket <= 0) {
            throw new IllegalArgumentException("candidatesPerTicket must be greater than 0");
        }

        List<List<Integer>> selectedTickets = new ArrayList<>();
        List<Long> selectedTicketMasks = new ArrayList<>();

        Set<Integer> coveredNumbers = new HashSet<>();
        Set<Long> coveredTriples = new HashSet<>();
        Set<Long> coveredQuadruples = new HashSet<>();
        Set<Long> coveredFives = new HashSet<>();

        while (selectedTickets.size() < ticketCount) {

            List<Integer> bestCandidate = null;
            long bestCandidateMask = 0L;
            double bestScore = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < candidatesPerTicket; i++) {
                List<Integer> candidate = LottoMachine.generateTicket(NUMBERS_PER_TICKET, MAX_NUMBER);
                long candidateMask = toMask(candidate);

                if (selectedTicketMasks.contains(candidateMask)) {
                    continue;
                }

                double score = calculateScore(
                        candidate,
                        candidateMask,
                        coveredNumbers,
                        coveredTriples,
                        coveredQuadruples,
                        coveredFives,
                        selectedTicketMasks
                );

                if (score > bestScore) {
                    bestScore = score;
                    bestCandidate = candidate;
                    bestCandidateMask = candidateMask;
                }
            }

            if (bestCandidate == null) {
                throw new IllegalStateException("Could not generate a unique ticket");
            }

            selectedTickets.add(bestCandidate);
            selectedTicketMasks.add(bestCandidateMask);

            coveredNumbers.addAll(bestCandidate);

            addSubsets(
                    bestCandidate,
                    3,
                    coveredTriples
            );

            addSubsets(
                    bestCandidate,
                    4,
                    coveredQuadruples
            );

            addSubsets(
                    bestCandidate,
                    5,
                    coveredFives
            );
        }

        return selectedTickets;
    }

    private static double calculateScore(List<Integer> candidate, long candidateMask, Set<Integer> coveredNumbers,
            Set<Long> coveredTriples, Set<Long> coveredQuadruples, Set<Long> coveredFives,
                                         List<Long> selectedTicketMasks) {
        int newNumbers =
                countNewNumbers(
                        candidate,
                        coveredNumbers
                );

        int newTriples =
                countNewSubsets(
                        candidate,
                        3,
                        coveredTriples
                );

        int newQuadruples =
                countNewSubsets(
                        candidate,
                        4,
                        coveredQuadruples
                );

        int newFives =
                countNewSubsets(
                        candidate,
                        5,
                        coveredFives
                );

        double overlapPenalty =
                calculateOverlapPenalty(
                        candidateMask,
                        selectedTicketMasks
                );

        return newNumbers * NEW_NUMBER_WEIGHT
                + newTriples * TRIPLE_WEIGHT
                + newQuadruples * QUADRUPLE_WEIGHT
                + newFives * FIVE_NUMBER_WEIGHT
                - overlapPenalty;
    }

    private static int countNewNumbers(List<Integer> candidate, Set<Integer> coveredNumbers) {
        int count = 0;

        for (Integer number : candidate) {
            if (!coveredNumbers.contains(number)) {
                count++;
            }
        }

        return count;
    }

    private static double calculateOverlapPenalty(long candidateMask, List<Long> selectedTicketMasks) {
        double penalty = 0;

        for (long existingMask : selectedTicketMasks) {
            int overlap = Long.bitCount(candidateMask & existingMask);

            /*
             * 0 sau 1 număr comun este ok.
             *
             * Începem să penalizăm de la două
             * numere comune în sus.
             */
            if (overlap > 1) {
                penalty += (overlap - 1) * OVERLAP_PENALTY;
            }
        }

        return penalty;
    }

    private static int countNewSubsets(List<Integer> ticket, int subsetSize, Set<Long> coveredSubsets) {
        return countNewSubsets(ticket, subsetSize, 0, 0, 0L, coveredSubsets);
    }

    private static int countNewSubsets(List<Integer> ticket, int subsetSize, int startIndex, int selectedCount,
            long currentMask, Set<Long> coveredSubsets) {
        if (selectedCount == subsetSize) {
            return coveredSubsets.contains(currentMask) ? 0 : 1;
        }

        int count = 0;
        int remainingNumbers =
                subsetSize - selectedCount;

        for (int i = startIndex; i <= ticket.size() - remainingNumbers; i++) {
            long newMask = currentMask | 1L << (ticket.get(i) - 1);

            count += countNewSubsets(ticket, subsetSize, i + 1, selectedCount + 1,
                    newMask, coveredSubsets);
        }

        return count;
    }

    private static void addSubsets(List<Integer> ticket, int subsetSize, Set<Long> coveredSubsets) {
        addSubsets(ticket, subsetSize, 0, 0, 0L, coveredSubsets);
    }

    private static void addSubsets(List<Integer> ticket, int subsetSize, int startIndex, int selectedCount,
                                   long currentMask, Set<Long> coveredSubsets) {
        if (selectedCount == subsetSize) {
            coveredSubsets.add(currentMask);
            return;
        }

        int remainingNumbers = subsetSize - selectedCount;

        for (int i = startIndex; i <= ticket.size() - remainingNumbers; i++) {
            long newMask = currentMask | 1L << (ticket.get(i) - 1);
            addSubsets(ticket, subsetSize, i + 1, selectedCount + 1, newMask, coveredSubsets);
        }
    }

    private static long toMask(List<Integer> ticket) {
        long mask = 0L;

        for (Integer number : ticket) {
            mask |= 1L << (number - 1);
        }

        return mask;
    }

    private CombinatorialSmartTicketGenerator() {
    }
}
