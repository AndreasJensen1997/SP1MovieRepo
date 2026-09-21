package app.daos;

import app.entities.Movie;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class MovieDAO extends GenericDAO<Movie>{

    public MovieDAO(EntityManagerFactory emf) {
        super(emf, Movie.class, Movie.class.getSimpleName());
    }

    public List<Movie> allMoviesByDirectorId(Integer id){
        if (id == null){
            throw new DatabaseException("DirectorID is required");
        }
        try(EntityManager em = emf.createEntityManager()){
            try {
                TypedQuery<Movie> query = em.createQuery("SELECT DISTINCT m FROM Movie m JOIN m.directors d WHERE d.id = :id", Movie.class);
                query.setParameter("id", id);
                List<Movie> movies = query.getResultList();
                if (movies.isEmpty()){
                    throw new DatabaseException("No movies by found by directorId: " + id + " in db");
                } else {
                    return query.getResultList();
                }
            } catch (PersistenceException e){
                throw new DatabaseException("Get all movies by ID: " + id +  " failed: ");
            }
        }
    }


    public List<Movie> getMoviesByTitle(String title){
        if (title == null){
            throw new DatabaseException("Title is requried");
        }
        title = title.toLowerCase();
        try(EntityManager em = emf.createEntityManager()){
            try {
                TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(:title)", Movie.class);
                query.setParameter("title", "%" + title + "%");
                List<Movie> movies = query.getResultList();
                if (movies.isEmpty()){
                    throw new DatabaseException("No movies by found by title: " + title + " in db");
                } else {
                    return query.getResultList();
                }
            } catch (PersistenceException e){
                throw new DatabaseException("Get all movies by title: " + title +  " failed: " + e.getMessage());
            }
        }
    }

    public double getAverageRatingOfAllMovies(){
        try(EntityManager em = emf.createEntityManager()){
            try {
                TypedQuery<Double> query = em.createQuery("SELECT AVG(m.rating) FROM Movie m", Double.class);
                return query.getSingleResult();

            } catch (PersistenceException e){
                throw new DatabaseException("Get average rating failed: " + e.getMessage());
            }
        }
    }

    public List<Movie> getTopTenHighestRatedMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.rating DESC", Movie.class);
                query.setMaxResults(10);

                return query.getResultList();
            } catch (PersistenceException e) {
                throw new DatabaseException("Failed to fetch top 10 highest-rated movies: " + e.getMessage());
            }
        }
    }


    public List<Movie> getTopTenLowestRatedMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.rating ASC", Movie.class);
                query.setMaxResults(10);

                return query.getResultList();
            } catch (PersistenceException e) {
                throw new DatabaseException("Failed to fetch top 10 lowest-rated movies: " + e.getMessage());
            }
        }
    }
}
