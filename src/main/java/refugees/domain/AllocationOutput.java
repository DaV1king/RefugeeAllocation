package refugees.domain;

import java.util.List;

public record AllocationOutput(List<CountryAllocation> lyckade, List<Refugee> misslyckade) {
}
