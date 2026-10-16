package refugees.allocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import refugees.domain.AllocationOutput;
import refugees.domain.CountryAllocation;
import refugees.domain.CountryCapacity;
import refugees.domain.Refugee;

class AllocationServiceTest {

    @Test
    void keepsFamilyTogetherAndRejectsWholeGroupWhenNoSpace() {
        AllocationService service = new AllocationService();

        List<CountryCapacity> countries = List.of(
                new CountryCapacity("Sverige", 1),
                new CountryCapacity("Tyskland", 2),
                new CountryCapacity("Polen", 1));

        List<Refugee> refugees = List.of(
                new Refugee("Solo", "id-1", ""),
                new Refugee("Family A", "id-2", "fam1"),
                new Refugee("Family B", "id-3", "fam1"),
                new Refugee("Family C", "id-4", "fam2"),
                new Refugee("Family D", "id-5", "fam2"));

        AllocationOutput output = service.allocate(countries, refugees);

        Set<String> successIds = output.lyckade().stream()
                .map(CountryAllocation::fordelning)
                .flatMap(List::stream)
                .map(Refugee::identifier)
                .collect(Collectors.toSet());

        Set<String> failedIds = output.misslyckade().stream()
                .map(Refugee::identifier)
                .collect(Collectors.toSet());

        assertEquals(3, successIds.size());
        assertEquals(2, failedIds.size());

        boolean fam1Together = output.lyckade().stream()
                .anyMatch(country -> country.fordelning().stream().filter(r -> "fam1".equals(r.groupId())).count() == 2);
        boolean fam2TogetherInSuccess = output.lyckade().stream()
                .anyMatch(country -> country.fordelning().stream().filter(r -> "fam2".equals(r.groupId())).count() == 2);
        boolean fam2Rejected = output.misslyckade().stream().filter(r -> "fam2".equals(r.groupId())).count() == 2;

        assertTrue(fam1Together);
        assertTrue(!fam2TogetherInSuccess);
        assertTrue(fam2Rejected);
    }

    @Test
    void doesNotExceedCountryCapacity() {
        AllocationService service = new AllocationService();

        List<CountryCapacity> countries = List.of(
                new CountryCapacity("A", 2),
                new CountryCapacity("B", 1));

        List<Refugee> refugees = List.of(
                new Refugee("R1", "id-1", ""),
                new Refugee("R2", "id-2", ""),
                new Refugee("R3", "id-3", ""),
                new Refugee("R4", "id-4", ""));

        AllocationOutput output = service.allocate(countries, refugees);

        int allocated = output.lyckade().stream().mapToInt(country -> country.fordelning().size()).sum();
        assertEquals(3, allocated);
        assertEquals(1, output.misslyckade().size());
    }
}
