package refugees.domain;

import java.util.List;

public record CountryAllocation(String land, List<Refugee> fordelning) {
}
