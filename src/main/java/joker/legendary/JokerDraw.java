package joker.legendary;

import java.time.LocalDate;
import java.util.List;

public record JokerDraw(
        LocalDate date,
        List<Integer> numbers,
        int joker
) {
    public JokerDraw {
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }

        if (numbers == null || numbers.size() != 5) {
            throw new IllegalArgumentException(
                    "Joker draw must contain exactly 5 main numbers"
            );
        }

        List<Integer> normalized = numbers.stream()
                .sorted()
                .toList();

        if (normalized.stream().distinct().count() != 5) {
            throw new IllegalArgumentException(
                    "Main numbers must be distinct"
            );
        }

        if (normalized.stream().anyMatch(n -> n < 1 || n > 45)) {
            throw new IllegalArgumentException(
                    "Main numbers must be between 1 and 45"
            );
        }

        if (joker < 1 || joker > 20) {
            throw new IllegalArgumentException(
                    "Joker must be between 1 and 20"
            );
        }

        numbers = normalized;
    }
}