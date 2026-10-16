package refugees;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import refugees.fileio.CsvCountryReader;
import refugees.fileio.JsonRefugeeReader;
import refugees.fileio.JsonResultWriter;
import refugees.domain.AllocationOutput;
import refugees.domain.CountryCapacity;
import refugees.domain.Refugee;
import refugees.allocation.AllocationService;

public class App {
    public static void main(String[] args) throws IOException {
        CliArgs cliArgs = CliArgs.parse(args);

        List<CountryCapacity> countries = CsvCountryReader.read(cliArgs.countriesPath());
        List<Refugee> refugees = JsonRefugeeReader.read(cliArgs.refugeesPath());

        AllocationService service = new AllocationService();
        AllocationOutput output = service.allocate(countries, refugees);

        JsonResultWriter.write(cliArgs.outputPath(), output);

        System.out.println("Allocation complete. Output written to: " + cliArgs.outputPath());
        System.out.println("Countries: " + countries.size() + ", refugees: " + refugees.size());
    }

    private record CliArgs(Path countriesPath, Path refugeesPath, Path outputPath) {
        private static CliArgs parse(String[] args) {
            Path countries = Paths.get("src/main/resources/countries.csv");
            Path refugees = Paths.get("src/main/resources/refugees.json");
            Path output = Paths.get("output.json");

            for (int i = 0; i < args.length - 1; i++) {
                String key = args[i];
                String value = args[i + 1];
                switch (key) {
                    case "--countries" -> countries = Paths.get(value);
                    case "--refugees" -> refugees = Paths.get(value);
                    case "--output" -> output = Paths.get(value);
                    default -> {
                    }
                }
            }

            return new CliArgs(countries, refugees, output);
        }
    }
}
