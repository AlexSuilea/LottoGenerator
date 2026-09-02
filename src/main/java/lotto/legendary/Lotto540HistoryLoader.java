package lotto.legendary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Lotto540HistoryLoader {

    private static final String URL =
            "https://www.loto49.ro/arhiva-superloto.php";

    private Lotto540HistoryLoader() {}

    public static List<LottoDraw> load(
            LocalDate from
    ) throws IOException {

        Document document = Jsoup.connect(URL)
                .userAgent("Mozilla/5.0")
                .timeout(20_000)
                .get();

        List<LottoDraw> result = new ArrayList<>();

        for (Element row : document.select("tr")) {
            Elements cells = row.select("td");

            if (cells.size() < 7) {
                continue;
            }

            String dateText = cells.get(0).text().trim();

            if (!dateText.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                continue;
            }

            LocalDate date = parseDate(dateText);

            if (date.isBefore(from)) {
                continue;
            }

            List<Integer> numbers = new ArrayList<>();

            for (int i = 1; i <= 6; i++) {
                numbers.add(
                        Integer.parseInt(cells.get(i).text().trim())
                );
            }

            // IMPORTANT: preserve draw order!
            result.add(new LottoDraw(date, numbers));
        }

        return result.stream()
                .distinct()
                .sorted(Comparator.comparing(LottoDraw::date))
                .toList();
    }

    private static LocalDate parseDate(String value) {
        String[] parts = value.split("-");

        return LocalDate.of(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
        );
    }
}