package app.utils;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Movie;
import app.enums.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class TestPopulator {

    public record SeededData(

            Actor actor1, Actor actor2,
            Director director1, Director director2,
            Movie movie1, Movie movie2
    ) {
    }

    public static SeededData populate(EntityManagerFactory emf) {

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.createNativeQuery("TRUNCATE TABLE movie,actor,director RESTART IDENTITY CASCADE").executeUpdate();
            } catch (PersistenceException e) {

            }


            Actor actor1 = Actor.builder().name("jason").character("the bad guy").build();
            Actor actor2 = Actor.builder().name("terkel").character("the good guy").build();

            em.persist(actor1);
            em.persist(actor2);

            Director director1 = Director.builder().name("Lars Von Trier").build();
            Director director2 = Director.builder().name("Christopher Nolan").build();

            em.persist(director1);
            em.persist(director2);

            Movie movie1 = Movie.builder().title("Terkel i knibe").genres(List.of(Genre.CRIME, Genre.ANIMATION)).build();
            Movie movie2 = Movie.builder().title("Inception").genres(List.of(Genre.ACTION,Genre.SCIENCE_FICTION)).build();
            movie1.addDirectors(List.of(director1, director2));
            em.persist(movie1);
            em.persist(movie2);

            em.getTransaction().commit();

            return new SeededData(
                    actor1,actor2,
                    director1,director2,
                    movie1,movie2);

        }
    }
}
