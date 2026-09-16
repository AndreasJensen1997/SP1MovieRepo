package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDTO(

        @JsonProperty("original_title")
        String title,

        @JsonProperty("id")
        int id,

        String overview,

        @JsonProperty("original_language")
        String language,

        @JsonProperty("release_date")
        String releaseDate,

        List<GenreDTO> genres,

        @JsonProperty("vote_average")
        double rating,

        @JsonProperty("poster_path")
        String posterPath,

        @JsonProperty("vote_count")
        int voteCount,

        @JsonProperty("credits")
        Credit credit) {
    @Override
    public String toString() {
        //json formatting removed the first bit of the string
        String s = overview.replace("\r"," ");
        return "MovieDTO{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", overview='" + s + '\'' +
                ", language='" + language + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", genres=" + genres +
                ", rating=" + rating +
                ", posterPath='" + posterPath + '\'' +
                ", voteCount=" + voteCount +
                ", credit=" + credit +
                '}';
    }
}
