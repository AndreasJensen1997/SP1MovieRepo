package app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

public record Crew(@JsonProperty("known_for_department")
                   String knownForDepartment,

                   @JsonProperty("original_name")
                   String originalName,

                   @JsonProperty("profile_path")
                   String imageUrl,

                   @JsonProperty("job")
                   String job) {}
