package refugees.fileio;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import refugees.domain.Refugee;
import refugees.domain.RefugeesInput;

public final class JsonRefugeeReader {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonRefugeeReader() {
    }

    public static List<Refugee> read(Path path) throws IOException {
        RefugeesInput input = MAPPER.readValue(path.toFile(), RefugeesInput.class);
        List<Refugee> refugees = input.refugees() == null ? List.of() : input.refugees();
        List<Refugee> sanitized = new ArrayList<>(refugees.size());

        for (Refugee r : refugees) {
            if (r == null) {
                continue;
            }
            String name = r.name() == null ? "" : r.name().trim();
            String identifier = r.identifier() == null ? "" : r.identifier().trim();
            if (name.isEmpty() || identifier.isEmpty()) {
                continue;
            }
            sanitized.add(new Refugee(name, identifier, r.groupId()));
        }

        return sanitized;
    }
}
