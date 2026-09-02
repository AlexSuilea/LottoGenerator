package lotto.legendary;

import java.time.LocalDate;
import java.util.List;

public record LottoDraw(
        LocalDate date,
        List<Integer> numbers
) {
    public LottoDraw {
        numbers = List.copyOf(numbers);
    }
}
