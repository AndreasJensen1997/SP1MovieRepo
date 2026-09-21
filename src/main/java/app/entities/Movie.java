package app.entities;

import app.enums.Genre;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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


    @PreRemove
    public void onDeletion(){
        deleteActors();
        deleteDirectors();
    }

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

    public void deleteDirectors(){
        if (!this.directors.isEmpty()){
            for (Director director : directors) {
                director.movies.remove(this);
            }
            directors.clear();
        }
    }

    public void deleteActors(){
        if (!this.actors.isEmpty()){
            for (Actor actor : actors) {
                actor.getMovies().remove(this);
            }
            actors.clear();
        }
    }

    // ===== EQUALS & HASHCODE =====
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer()
                .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        Movie movie = (Movie) o;
        return getId() != null && Objects.equals(getId(), movie.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode() : getClass().hashCode();


    }
}
