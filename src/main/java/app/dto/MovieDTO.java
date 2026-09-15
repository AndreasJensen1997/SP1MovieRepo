package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDTO(
        @JsonProperty("original_title") String title,
        String overview,
        @JsonProperty("original_language") String language,
        @JsonProperty("release_date") String releaseDate,
        List<GenreDTO> genres,
        @JsonProperty("vote_average") double rating,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("vote_count") int voteCount,
        Credit credit) {}
