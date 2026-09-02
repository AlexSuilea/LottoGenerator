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

public final class Lotto649HistoryLoader {

    private static final List<String> URLS = List.of(
            "https://www.loto49.ro/arhiva-loto-6-49-din-perioada-2020-2023.php",
            "https://www.loto49.ro/arhiva-loto49.php"
    );

    private Lotto649HistoryLoader() {}

    public static List<LottoDraw> load() throws IOException {
        List<LottoDraw> result = new ArrayList<>();

        for (String url : URLS) {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(20_000)
                    .get();

            parse(document, result, 49);
        }

        return result.stream()
                .distinct()
                .sorted(Comparator.comparing(LottoDraw::date))
                .toList();
    }

    private static void parse(
            Document document,
            List<LottoDraw> result,
            int maxNumber
    ) {
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

            List<Integer> numbers = new ArrayList<>();

            for (int i = 1; i <= 6; i++) {
                int number = Integer.parseInt(cells.get(i).text().trim());

                if (number < 1 || number > maxNumber) {
                    throw new IllegalStateException("Invalid number: " + number);
                }

                numbers.add(number);
            }

            result.add(
                    new LottoDraw(
                            date,
                            numbers.stream().sorted().toList()
                    )
            );
        }
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