package refugees.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Refugee(String name, String identifier, String groupId) {
    @JsonCreator
    public Refugee(
            @JsonProperty("name") String name,
            @JsonProperty("identifier") String identifier,
            @JsonProperty("groupId") String groupId) {
        this.name = name;
        this.identifier = identifier;
        this.groupId = groupId == null ? "" : groupId.trim();
    }
}
