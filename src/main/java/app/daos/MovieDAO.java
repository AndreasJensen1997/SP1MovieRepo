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
                return query.getResultList();
            } catch (PersistenceException e){
                throw new DatabaseException("Get all movies by ID: " + id +  " failed: " + e.getMessage());
            }
        }
    }
}
