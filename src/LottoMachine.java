import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class LottoMachine {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String INVALID_ARGUMENTS = "numberOfNumbers must be between 1 and maxNumber";

    private static final BigDecimal TRANSACTION_FEE = BigDecimal.valueOf(0.5);

    private static final BigDecimal JOKER_PRICE = BigDecimal.valueOf(7);

    private static final int JOKER_NUMBERS_PER_TICKET = 5;
    private static final int JOKER_MAX_NUMBER = 45;
    private static final int JOKER_MAX_EXTRA_NUMBER = 20;
    private static final int JOKER_ZONES_PER_SLIP = 2;

    public static BigDecimal printTickets(String game, int ticketCount, int numbersPerTicket, int maxNumber,
                                          BigDecimal pricePerVariant, int zonesPerSlip) {
        validateTicketCount(ticketCount);

        if (ticketCount == 0) {
            return BigDecimal.ZERO;
        }

        System.out.println(game);

        Set<List<Integer>> tickets = new LinkedHashSet<>();

        while (tickets.size() < ticketCount) {
            tickets.add(generateTicket(numbersPerTicket, maxNumber));
        }

        tickets.forEach(System.out::println);

        BigDecimal cost = calculateCost(ticketCount, pricePerVariant, zonesPerSlip);

        System.out.println("Cost: " + formatPrice(cost));
        System.out.println();

        return cost;
    }

    public static BigDecimal printJokerTickets(int ticketCount) {
        validateTicketCount(ticketCount);

        if (ticketCount == 0) {
            return BigDecimal.ZERO;
        }

        System.out.println("Joker:");

        Set<String> jokerTickets = new LinkedHashSet<>();

        while (jokerTickets.size() < ticketCount) {
            int jokerNumber = RANDOM.nextInt(1, JOKER_MAX_EXTRA_NUMBER + 1);

            jokerTickets.add(
                    generateTicket(
                            JOKER_NUMBERS_PER_TICKET,
                            JOKER_MAX_NUMBER
                    )
                            + " | Joker: "
                            + jokerNumber
            );
        }

        jokerTickets.forEach(System.out::println);

        BigDecimal cost = calculateCost(ticketCount, JOKER_PRICE, JOKER_ZONES_PER_SLIP);

        System.out.println("Cost: " + formatPrice(cost));
        System.out.println();

        return cost;
    }

    public static List<Integer> generateTicket(int numberOfNumbers, int maxNumber) {
        if (numberOfNumbers <= 0 || numberOfNumbers > maxNumber) {
            throw new IllegalArgumentException(INVALID_ARGUMENTS);
        }

        List<Integer> numbers = new ArrayList<>(
                IntStream.rangeClosed(1, maxNumber)
                        .boxed()
                        .toList()
        );

        Collections.shuffle(numbers, RANDOM);

        return numbers.stream()
                .limit(numberOfNumbers)
                .sorted()
                .toList();
    }

    private static BigDecimal calculateCost(int numberOfVariants, BigDecimal pricePerVariant, int zonesPerSlip) {
        if (zonesPerSlip <= 0) {
            throw new IllegalArgumentException("zonesPerSlip must be greater than 0");
        }

        if (pricePerVariant == null) {
            throw new IllegalArgumentException("pricePerVariant must not be null");
        }

        if (pricePerVariant.signum() < 0) {
            throw new IllegalArgumentException("pricePerVariant must be non-negative");
        }

        if (numberOfVariants <= 0) {
            return BigDecimal.ZERO;
        }

        int numberOfSlips = (numberOfVariants + zonesPerSlip - 1) / zonesPerSlip;

        BigDecimal variantsCost = pricePerVariant.multiply(BigDecimal.valueOf(numberOfVariants));

        BigDecimal feesCost = TRANSACTION_FEE.multiply(BigDecimal.valueOf(numberOfSlips));

        return variantsCost.add(feesCost);
    }

    public static String formatPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("price must not be null");
        }

        return price.setScale(2, RoundingMode.HALF_UP) + " RON";
    }

    private static void validateTicketCount(int ticketCount) {
        if (ticketCount < 0) {
            throw new IllegalArgumentException("ticketCount must not be negative");
        }
    }

    public static Set<List<Integer>> generateTickets(
            int ticketCount,
            int numbersPerTicket,
            int maxNumber
    ) {
        Set<List<Integer>> tickets = new LinkedHashSet<>();

        while (tickets.size() < ticketCount) {
            tickets.add(generateTicket(numbersPerTicket, maxNumber));
        }

        return tickets;
    }

    public static void testUniformity(int simulations, int numbersPerTicket, int maxNumber) {
        long[] frequencies = new long[maxNumber + 1];

        for (int i = 0; i < simulations; i++) {
            List<Integer> ticket = generateTicket(numbersPerTicket, maxNumber);

            for (int number : ticket) {
                frequencies[number]++;
            }
        }

        double expectedPercentage =
                (double) numbersPerTicket / maxNumber * 100.0;

        double expectedCount =
                (double) simulations * numbersPerTicket / maxNumber;

        System.out.printf(
                "Expected per number: %.2f occurrences (%.4f%%)%n%n",
                expectedCount,
                expectedPercentage
        );

        for (int number = 1; number <= maxNumber; number++) {
            double actualPercentage =
                    frequencies[number] * 100.0 / simulations;

            double difference =
                    actualPercentage - expectedPercentage;

            System.out.printf(
                    "%2d -> %8d | %7.4f%% | diff: %+7.4f%%%n",
                    number,
                    frequencies[number],
                    actualPercentage,
                    difference
            );
        }
    }
}