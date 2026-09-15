package app.entities;

import app.enums.Genre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    private String language;
    private String overview;
    @ManyToMany
    private List<Director> directors;
    @ManyToMany
    private List<Actor> actors;
    private String title;
    private LocalDate releaseDate;
    private double rating;
    @ElementCollection(targetClass = Genre.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Genre> genres;
    private String posterURL;
}
