package app.daos;

import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;

public class MovieDAO extends GenericDAO<Movie>{

    public MovieDAO(EntityManagerFactory emf) {
        super(emf, Movie.class, Movie.class.getSimpleName());
    }
}
