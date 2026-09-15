package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Cast(@JsonProperty("known_for_department") String knownForDepartment,
                   @JsonProperty("original_name") String originalName,
                   String character,
                   @JsonProperty("profile_path") String imageUrl,
                   String job) {}
