package app.daos;

import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public abstract class GenericDAO<T> implements IDAO<T, Integer> {
    protected EntityManagerFactory emf;
    private final Class<T> clazz;
    private String entityName;

    @Override
    public T create(T entity) {
        if (entity == null) {
            throw new DatabaseException(entityName+" is required");
        }
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            throw new DatabaseException("Error creating "+entityName, e);
        }
    }

    @Override
    public T read(Integer id) {
        if (id == null) {
            throw new DatabaseException("ID is required");
        }
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            T entity = em.find(clazz, id);
            em.getTransaction().commit();
            if (entity != null) {
                return entity;
            }
            throw new DatabaseException(entityName+" not found with id: " + id);
        } catch (PersistenceException e) {
            throw new DatabaseException("Reading "+entityName+" failed", e);
        }
    }

    @Override
    public Set<T> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            String jpql = "select t from " + clazz.getSimpleName() + " t";
            Set<T> entities = new HashSet<>(em.createQuery(jpql, clazz).getResultList());
            em.getTransaction().commit();
            return entities;
        } catch (Exception e) {
            throw new DatabaseException("Reading all "+entityName+"s failed", e);
        }
    }

    @Override
    public T update(T entity) {
        if (entity == null) {
            throw new DatabaseException(entityName+" is required for update");
        }
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            T merged = em.merge(entity);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            throw new DatabaseException("Updating "+entityName+" failed", e);
        }
    }

    @Override
    public boolean delete(T entity) {
        if (entity == null) {
            throw new DatabaseException(entityName+" is required for deletion");
        }
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                T managed = em.contains(entity) ? entity : em.merge(entity);
                if (managed != null) {
                    em.remove(managed);
                    em.getTransaction().commit();
                    return true;
                } else {
                    throw new DatabaseException(entityName+" not found for deletion");
                }
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new DatabaseException("Delete "+entityName+" failed", e);
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
    }
}


