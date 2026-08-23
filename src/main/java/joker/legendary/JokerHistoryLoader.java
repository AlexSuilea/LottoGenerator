package joker.legendary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class JokerHistoryLoader {

    private static final String ARCHIVE_URL =
            "https://www.loto49.ro/arhiva-joker.php";

    private JokerHistoryLoader() {
    }

    public static List<JokerDraw> load(
            LocalDate from,
            LocalDate to
    ) throws IOException {

        Document document = Jsoup.connect(ARCHIVE_URL)
                .userAgent("Mozilla/5.0")
                .timeout(20_000)
                .get();

        List<JokerDraw> draws = new ArrayList<>();

        for (Element row : document.select("tr")) {

            Elements cells = row.select("td");

            if (cells.size() < 7) {
                continue;
            }

            String dateText = cells.get(0)
                    .text()
                    .trim();

            if (!dateText.matches("\\d{4}-\\d{2}-\\d{2}")) {
                continue;
            }

            LocalDate date = LocalDate.parse(dateText);

            if (date.isBefore(from) || date.isAfter(to)) {
                continue;
            }

            List<Integer> numbers = List.of(
                    parseNumber(cells.get(1).text()),
                    parseNumber(cells.get(2).text()),
                    parseNumber(cells.get(3).text()),
                    parseNumber(cells.get(4).text()),
                    parseNumber(cells.get(5).text())
            );

            int joker =
                    parseNumber(cells.get(6).text());

            draws.add(
                    new JokerDraw(
                            date,
                            numbers,
                            joker
                    )
            );
        }

        return draws.stream()
                .sorted(Comparator.comparing(JokerDraw::date))
                .toList();
    }

    private static int parseNumber(String value) {

        String digits =
                value.replaceAll("[^0-9]", "");

        if (digits.isBlank()) {
            throw new IllegalArgumentException(
                    "Cannot parse number from: " + value
            );
        }

        return Integer.parseInt(digits);
    }
}
