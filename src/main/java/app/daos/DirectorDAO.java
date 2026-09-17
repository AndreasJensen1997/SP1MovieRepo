package app.daos;

import app.entities.Director;
import app.entities.Movie;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class DirectorDAO extends GenericDAO<Director> {
    public DirectorDAO(EntityManagerFactory emf) {
        super(emf, Director.class, Director.class.getSimpleName());
    }


}
