package refugees.allocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import refugees.domain.AllocationOutput;
import refugees.domain.CountryAllocation;
import refugees.domain.CountryCapacity;
import refugees.domain.Refugee;

public class AllocationService {
    public AllocationOutput allocate(List<CountryCapacity> countries, List<Refugee> refugees) {
        if (countries == null || countries.isEmpty()) {
            throw new IllegalArgumentException("Countries cannot be empty");
        }

        List<RefugeeGroup> groups = groupRefugees(refugees == null ? List.of() : refugees);

        List<CountryState> states = countries.stream()
                .map(c -> new CountryState(c.country(), c.capacity()))
                .toList();

        List<CountryState> weightedRotation = buildWeightedRotation(states);
        if (weightedRotation.isEmpty()) {
            throw new IllegalArgumentException("All countries have zero capacity");
        }

        int rotationIndex = 0;
        List<Refugee> rejected = new ArrayList<>();

        for (RefugeeGroup group : groups) {
            int groupSize = group.members().size();
            boolean allocated = false;

            for (int hop = 0; hop < weightedRotation.size(); hop++) {
                CountryState candidate = weightedRotation.get((rotationIndex + hop) % weightedRotation.size());
                if (candidate.hasCapacityFor(groupSize)) {
                    candidate.assign(group.members());
                    rotationIndex = (rotationIndex + hop + 1) % weightedRotation.size();
                    allocated = true;
                    break;
                }
            }

            if (!allocated) {
                rejected.addAll(group.members());
            }
        }

        List<CountryAllocation> successful = states.stream()
                .filter(s -> !s.assigned().isEmpty())
                .map(s -> new CountryAllocation(s.name(), List.copyOf(s.assigned())))
                .toList();

        return new AllocationOutput(successful, rejected);
    }

    private static List<RefugeeGroup> groupRefugees(List<Refugee> refugees) {
        Map<String, List<Refugee>> grouped = new LinkedHashMap<>();
        int singletonCount = 0;

        for (Refugee refugee : refugees) {
            String groupId = refugee.groupId();
            String key;
            if (groupId == null || groupId.isBlank()) {
                key = "single-" + singletonCount++;
            } else {
                key = "group-" + groupId;
            }
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(refugee);
        }

        return grouped.values().stream().map(RefugeeGroup::new).toList();
    }

    private static List<CountryState> buildWeightedRotation(List<CountryState> states) {
        List<CountryState> rotation = new ArrayList<>();

        for (CountryState state : states) {
            for (int i = 0; i < state.capacity(); i++) {
                rotation.add(state);
            }
        }

        return rotation;
    }

    private record RefugeeGroup(List<Refugee> members) {
    }

    private static final class CountryState {
        private final String name;
        private final int capacity;
        private final List<Refugee> assigned = new ArrayList<>();

        private CountryState(String name, int capacity) {
            this.name = name;
            this.capacity = Math.max(capacity, 0);
        }

        private boolean hasCapacityFor(int groupSize) {
            return assigned.size() + groupSize <= capacity;
        }

        private void assign(List<Refugee> refugees) {
            assigned.addAll(refugees);
        }

        private String name() {
            return name;
        }

        private int capacity() {
            return capacity;
        }

        private List<Refugee> assigned() {
            return assigned;
        }
    }
}
