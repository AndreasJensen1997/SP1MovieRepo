package app.daos;

import app.entities.Actor;
import jakarta.persistence.EntityManagerFactory;

public class ActorDAO extends GenericDAO<Actor> {
    public ActorDAO(EntityManagerFactory emf) {
        super(emf, Actor.class, Actor.class.getSimpleName());
    }
}
