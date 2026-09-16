package app.daos;

import app.entities.Director;
import jakarta.persistence.EntityManagerFactory;

public class DirectorDAO extends GenericDAO<Director> {
    public DirectorDAO(EntityManagerFactory emf) {
        super(emf, Director.class, Director.class.getSimpleName());
    }
}
