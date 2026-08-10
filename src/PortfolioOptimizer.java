import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

public final class PortfolioOptimizer {

    private static final int NUMBERS_PER_TICKET = 6;
    private static final int MAX_NUMBER = 49;
    private static final int TARGET_MATCHES = 3;

    /*
     * Păstrăm această proprietate deoarece overlap <= 1
     * este foarte bun pentru 4+/6, 5+/6 și 6/6.
     */
    private static final int MAX_PAIRWISE_OVERLAP = 1;

    public static OptimizationResult optimize(List<List<Integer>> initialTickets, int sampleDrawCount, int iterations,
                                              long seed) {
        validateArguments(initialTickets, sampleDrawCount, iterations);

        SplittableRandom random = new SplittableRandom(seed);

        List<List<Integer>> currentTickets = deepCopy(initialTickets);

        long[] ticketMasks = currentTickets.stream()
                        .mapToLong(PortfolioOptimizer::toMask)
                        .toArray();

        validateOverlap(ticketMasks);

        /*
         * Extragerile folosite la fitness rămân aceleași
         * pe toată durata optimizării.
         */
        long[] sampleDraws = generateSampleDraws(sampleDrawCount, random.split());

        /*
         * coverageCounts[i] spune câte bilete din portofoliu
         * au minimum 3 numere corecte pentru extragerea i.
         */
        int[] coverageCounts = calculateCoverageCounts(ticketMasks, sampleDraws);
        int currentCoveredDraws = countCoveredDraws(coverageCounts);
        int[] numberFrequencies = calculateNumberFrequencies(currentTickets);
        int currentDistinctNumbers = countDistinctNumbers(numberFrequencies);

        List<List<Integer>> bestTickets = deepCopy(currentTickets);

        int bestCoveredDraws = currentCoveredDraws;
        int bestDistinctNumbers = currentDistinctNumbers;

        int acceptedMutations = 0;

        for (int iteration = 0; iteration < iterations; iteration++) {
            int ticketIndex = random.nextInt(currentTickets.size());

            List<Integer> oldTicket = currentTickets.get(ticketIndex);
            long oldMask = ticketMasks[ticketIndex];

            Mutation mutation = mutateTicket(oldTicket, random);
            long newMask = toMask(mutation.ticket());

            /*
             * Nu vrem să stricăm structura:
             * maximum un număr comun între două bilete.
             */
            if (!hasValidOverlap(newMask, ticketIndex, ticketMasks)) {
                continue;
            }

            int distinctDelta = calculateDistinctNumberDelta(mutation, numberFrequencies);

            /*
             * Nu acceptăm mutări care reduc coverage-ul
             * numerelor distincte.
             */
            if (distinctDelta < 0) {
                continue;
            }

            int coverageDelta = calculateCoverageDelta(oldMask, newMask, sampleDraws, coverageCounts);

            /*
             * Hill climbing:
             *
             * - acceptăm dacă îmbunătățește 3+ coverage;
             * - acceptăm dacă coverage rămâne egal dar
             *   crește numărul de numere distincte;
             * - acceptăm rar o mutare neutră pentru a putea
             *   merge pe platouri.
             */
            boolean accept = coverageDelta > 0 || (coverageDelta == 0 && distinctDelta > 0) || (coverageDelta == 0
                    && distinctDelta == 0 && random.nextDouble() < 0.05);

            if (!accept) {
                continue;
            }

            applyCoverageMutation(oldMask, newMask, sampleDraws, coverageCounts);
            currentTickets.set(ticketIndex, mutation.ticket());
            ticketMasks[ticketIndex] = newMask;

            numberFrequencies[mutation.removedNumber()]--;
            numberFrequencies[mutation.addedNumber()]++;

            currentCoveredDraws += coverageDelta;
            currentDistinctNumbers += distinctDelta;

            acceptedMutations++;

            boolean betterCoverage = currentCoveredDraws > bestCoveredDraws;

            boolean sameCoverageBetterNumbers = currentCoveredDraws == bestCoveredDraws
                    && currentDistinctNumbers > bestDistinctNumbers;

            if (betterCoverage || sameCoverageBetterNumbers) {
                bestCoveredDraws = currentCoveredDraws;
                bestDistinctNumbers = currentDistinctNumbers;
                bestTickets = deepCopy(currentTickets);
            }
        }

        return new OptimizationResult(bestTickets, bestCoveredDraws, sampleDrawCount, bestDistinctNumbers,
                acceptedMutations);
    }

    private static Mutation mutateTicket(List<Integer> ticket, SplittableRandom random) {
        int position = random.nextInt(ticket.size());
        int removedNumber = ticket.get(position);
        int addedNumber;

        do {
            addedNumber = random.nextInt(1, MAX_NUMBER + 1);
        } while (ticket.contains(addedNumber));

        List<Integer> mutatedTicket = new ArrayList<>(ticket);
        mutatedTicket.set(position, addedNumber);

        Collections.sort(mutatedTicket);

        return new Mutation(List.copyOf(mutatedTicket), removedNumber, addedNumber);
    }

    private static int calculateCoverageDelta(long oldMask, long newMask, long[] sampleDraws, int[] coverageCounts) {
        int delta = 0;

        for (int i = 0; i < sampleDraws.length; i++) {

            long drawMask = sampleDraws[i];
            boolean oldCovered = matchesAtLeastThree(oldMask, drawMask);
            boolean newCovered = matchesAtLeastThree(newMask, drawMask);

            if (oldCovered == newCovered) {
                continue;
            }

            /*
             * Dacă vechiul bilet era singurul care
             * acoperea extragerea, iar noul nu o mai face,
             * pierdem un draw.
             */
            if (oldCovered && coverageCounts[i] == 1) {
                delta--;
            }

            /*
             * Dacă nicio variantă nu acoperea extragerea
             * și noul bilet o acoperă, câștigăm un draw.
             */
            if (newCovered && coverageCounts[i] == 0) {
                delta++;
            }
        }
        return delta;
    }

    private static void applyCoverageMutation(long oldMask, long newMask, long[] sampleDraws, int[] coverageCounts) {
        for (int i = 0; i < sampleDraws.length; i++) {
            boolean oldCovered = matchesAtLeastThree(oldMask, sampleDraws[i]);
            boolean newCovered = matchesAtLeastThree(newMask, sampleDraws[i]);

            if (oldCovered == newCovered) {
                continue;
            }

            if (oldCovered) {
                coverageCounts[i]--;
            }

            if (newCovered) {
                coverageCounts[i]++;
            }
        }
    }

    private static boolean matchesAtLeastThree(long ticketMask, long drawMask) {
        return Long.bitCount(ticketMask & drawMask) >= TARGET_MATCHES;
    }

    private static boolean hasValidOverlap(long candidateMask, int replacedTicketIndex, long[] ticketMasks) {
        for (int i = 0; i < ticketMasks.length; i++) {
            if (i == replacedTicketIndex) {
                continue;
            }

            int overlap = Long.bitCount(candidateMask & ticketMasks[i]);

            if (overlap > MAX_PAIRWISE_OVERLAP) {
                return false;
            }
        }

        return true;
    }

    private static int calculateDistinctNumberDelta(Mutation mutation, int[] frequencies) {
        boolean removedWasUnique = frequencies[mutation.removedNumber()] == 1;
        boolean addedIsNew = frequencies[mutation.addedNumber()] == 0;

        int delta = 0;

        if (removedWasUnique) {
            delta--;
        }

        if (addedIsNew) {
            delta++;
        }

        return delta;
    }

    private static long[] generateSampleDraws(int drawCount, SplittableRandom random) {
        long[] draws = new long[drawCount];

        for (int i = 0; i < drawCount; i++) {
            long mask = 0L;
            int selected = 0;

            while (selected < NUMBERS_PER_TICKET) {

                int number = random.nextInt(1, MAX_NUMBER + 1);
                long bit = 1L << (number - 1);

                if ((mask & bit) != 0) {
                    continue;
                }

                mask |= bit;
                selected++;
            }

            draws[i] = mask;
        }

        return draws;
    }

    private static int[] calculateCoverageCounts(long[] ticketMasks, long[] draws) {
        int[] counts = new int[draws.length];

        for (int i = 0; i < draws.length; i++) {
            for (long ticketMask : ticketMasks) {

                if (matchesAtLeastThree(ticketMask, draws[i])) {
                    counts[i]++;
                }
            }
        }

        return counts;
    }

    private static int countCoveredDraws(int[] coverageCounts) {
        int covered = 0;

        for (int count : coverageCounts) {
            if (count > 0) {
                covered++;
            }
        }

        return covered;
    }

    private static int[] calculateNumberFrequencies(List<List<Integer>> tickets) {
        int[] frequencies = new int[MAX_NUMBER + 1];

        for (List<Integer> ticket : tickets) {
            for (Integer number : ticket) {
                frequencies[number]++;
            }
        }

        return frequencies;
    }

    private static int countDistinctNumbers(int[] frequencies) {
        int count = 0;

        for (int number = 1;
             number <= MAX_NUMBER;
             number++) {

            if (frequencies[number] > 0) {
                count++;
            }
        }

        return count;
    }

    private static long toMask(List<Integer> ticket) {
        long mask = 0L;

        for (Integer number : ticket) {
            mask |= 1L << (number - 1);
        }

        return mask;
    }

    private static List<List<Integer>> deepCopy(List<List<Integer>> tickets) {
        List<List<Integer>> copy = new ArrayList<>();

        for (List<Integer> ticket : tickets) {
            copy.add(new ArrayList<>(ticket));
        }

        return copy;
    }

    private static void validateArguments(List<List<Integer>> tickets, int sampleDrawCount, int iterations) {
        if (tickets == null || tickets.isEmpty()) {

            throw new IllegalArgumentException("tickets must not be null or empty");
        }

        if (sampleDrawCount <= 0) {
            throw new IllegalArgumentException("sampleDrawCount must be greater than 0");
        }

        if (iterations <= 0) {
            throw new IllegalArgumentException("iterations must be greater than 0");
        }

        Set<Long> uniqueTickets = new HashSet<>();

        for (List<Integer> ticket : tickets) {

            if (ticket == null || ticket.size() != NUMBERS_PER_TICKET) {
                throw new IllegalArgumentException("Every ticket must contain exactly 6 numbers");
            }

            Set<Integer> uniqueNumbers = new HashSet<>(ticket);

            if (uniqueNumbers.size() != NUMBERS_PER_TICKET) {
                throw new IllegalArgumentException("A ticket cannot contain duplicate numbers");
            }

            for (Integer number : ticket) {
                if (number == null || number < 1 || number > MAX_NUMBER) {
                    throw new IllegalArgumentException("Ticket numbers must be between 1 and 49");
                }
            }

            long mask = toMask(ticket);

            if (!uniqueTickets.add(mask)) {
                throw new IllegalArgumentException("Portfolio contains duplicate tickets");
            }
        }
    }

    private static void validateOverlap(long[] ticketMasks) {
        for (int i = 0; i < ticketMasks.length; i++) {
            for (int j = i + 1; j < ticketMasks.length; j++) {

                int overlap = Long.bitCount(ticketMasks[i] & ticketMasks[j]);
                if (overlap > MAX_PAIRWISE_OVERLAP) {
                    throw new IllegalArgumentException("Initial portfolio must have maximum overlap <= 1");
                }
            }
        }
    }

    private record Mutation(List<Integer> ticket, int removedNumber, int addedNumber) {
    }

    public record OptimizationResult(List<List<Integer>> tickets, int coveredSampleDraws, int sampleDrawCount,
                                     int distinctNumbers, int acceptedMutations) {

        public double coveragePercentage() {
            return coveredSampleDraws * 100.0 / sampleDrawCount;
        }
    }

    private PortfolioOptimizer() {
    }
}