package refugees.fileio;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import refugees.domain.CountryCapacity;

public final class CsvCountryReader {
    private CsvCountryReader() {
    }

    public static List<CountryCapacity> read(Path path) throws IOException {
        List<CountryCapacity> countries = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(path)) {
            Iterable<CSVRecord> rows = CSVFormat.DEFAULT.parse(reader);
            for (CSVRecord row : rows) {
                if (row.size() < 2) {
                    continue;
                }
                String country = row.get(0).trim();
                String capText = row.get(1).trim();
                if (country.isEmpty() || capText.isEmpty()) {
                    continue;
                }

                int capacity = Integer.parseInt(capText);
                if (capacity < 0) {
                    throw new IllegalArgumentException("Capacity must be >= 0 for country: " + country);
                }
                countries.add(new CountryCapacity(country, capacity));
            }
        }

        if (countries.isEmpty()) {
            throw new IllegalArgumentException("No countries loaded from CSV: " + path);
        }

        return countries;
    }
}
