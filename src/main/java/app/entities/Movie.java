package app.entities;

import app.enums.Genre;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
    @Column(length = 1500)
    private String overview;
    @ManyToMany
    @Builder.Default
    private List<Director> directors = new ArrayList<>();
    @ManyToMany
    @Builder.Default
    private List<Actor> actors = new ArrayList<>();
    private String title;
    private LocalDate releaseDate;
    private double rating;
    @ElementCollection(targetClass = Genre.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Genre> genres = new ArrayList<>();
    private String posterURL;

    public void addDirectors(List<Director> directorsList) {
        if (directorsList != null) {
            for (Director director : directorsList) {
                directors.add(director);
                if (director != null) {
                    director.getMovies().add(this);
                }
            }
        }
    }

    public void addActors(List<Actor> actorsList) {
        if (actorsList != null) {
            for (Actor actor : actorsList) {
                actors.add(actor);
                if (actor != null) {
                    actor.getMovies().add(this);
                }
            }
        }
    }


}
