package refugees.domain;

import java.util.List;

public record RefugeesInput(String desc, String date, List<Refugee> refugees) {
}
