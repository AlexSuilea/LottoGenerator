import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.IntStream;

public class LottoMachine {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String INVALID_ARGUMENTS = "numberOfNumbers must be between 1 and maxNumber";
    private static final BigDecimal TRANSACTION_FEE = BigDecimal.valueOf(0.5);
    private static final BigDecimal JOKER_PRICE = BigDecimal.valueOf(7);

    public static BigDecimal printTickets(String game, int ticketCount, int numbersPerTicket, int maxNumber,
                                           BigDecimal pricePerVariant, int zonesPerSlip) {
        if (ticketCount < 0) {
            throw new IllegalArgumentException("ticketCount must not be negative");
        }

        if(ticketCount == 0) {
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
        if (ticketCount < 0) {
            throw new IllegalArgumentException("ticketCount must not be negative");
        }

        if(ticketCount == 0) {
            return BigDecimal.ZERO;
        }

        System.out.println("Joker:");
        Set<String> jokerTickets = new LinkedHashSet<>();

        while(jokerTickets.size() < ticketCount) {
            int jokerNumber = RANDOM.nextInt(1, 21);

            jokerTickets.add(generateTicket(5, 45) + " | Joker: " + jokerNumber);
        }
        jokerTickets.forEach(System.out::println);

        BigDecimal cost = calculateCost(ticketCount, JOKER_PRICE, 2);

        System.out.println("Cost: " + formatPrice(cost));
        System.out.println();

        return cost;
    }

    public static List<Integer> generateTicket(int numberOfNumbers, int maxNumber) {
        if (numberOfNumbers <= 0 || numberOfNumbers > maxNumber) {
            throw new IllegalArgumentException(INVALID_ARGUMENTS);
        }
        List<Integer> numbers = new ArrayList<>(
                IntStream.rangeClosed(1,maxNumber)
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

        if (pricePerVariant == null || pricePerVariant.signum() < 0) {
            throw new IllegalArgumentException("pricePerVariant must be non-negative");
        }

        if (numberOfVariants <= 0) {
            return BigDecimal.ZERO;
        }

        int numberOfSlips =
                (numberOfVariants + zonesPerSlip - 1) / zonesPerSlip;

        BigDecimal variantsCost = pricePerVariant.multiply(
                BigDecimal.valueOf(numberOfVariants)
        );

        BigDecimal feesCost = TRANSACTION_FEE.multiply(
                BigDecimal.valueOf(numberOfSlips)
        );

        return variantsCost.add(feesCost);
    }

    public static String formatPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("price must not be null");
        }

        return price
                .setScale(2, RoundingMode.HALF_UP)
                + " RON";
    }
}
