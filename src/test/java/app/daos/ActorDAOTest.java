package app.daos;

import app.config.HibernateTestConfig;
import app.entities.Actor;
import app.exceptions.DatabaseException;
import app.utils.TestPopulator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActorDAOTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private ActorDAO actorDAO;
    private TestPopulator.SeededData seeded;

    @BeforeEach
    void setUp() {
        seeded = TestPopulator.populate(emf);
    }

    @BeforeAll
    void setUpAll() {
        actorDAO = new ActorDAO(emf);
    }

    @Test
    void create() {
        Actor newActor = Actor.builder()
                .name("Keanu Reeves")
                .character("Neo")
                .pictureURL("keanu.url")
                .build();

        Actor createdActor = actorDAO.create(newActor);
        assertThat(createdActor.getId(), notNullValue());
        assertThat(createdActor.getName(), is(newActor.getName()));
        assertThat(createdActor.getCharacter(), is(newActor.getCharacter()));
    }

    @Test
    void read() {
        Actor actor = seeded.actor1();
        Actor fetchedActor = actorDAO.read(actor.getId());
        assertThat(fetchedActor, notNullValue());
        assertThat(fetchedActor.getId(), is(actor.getId()));
        assertThat(fetchedActor.getName(), is(actor.getName()));
    }

    @Test
    void readAll() {
        Set<Actor> actors = Set.of(seeded.actor1(), seeded.actor2());
        Set<Actor> fetchedActors = actorDAO.readAll();
        assertThat(fetchedActors, notNullValue());
        assertThat(fetchedActors.size(), is(actors.size()));
    }

    @Test
    void update() {
        Actor seed = seeded.actor1();
        Actor updated = Actor.builder()
                .id(seed.getId())
                .name("Jason Bourne")
                .character("Secret Agent")
                .pictureURL("bourne.url").build();

        Actor result = actorDAO.update(updated);

        assertThat(result.getId(), is(seed.getId()));
        assertThat(result.getName(), is("Jason Bourne"));
        assertThat(result.getCharacter(), is("Secret Agent"));
        assertThat(result.getPictureURL(), is("bourne.url"));
    }

    @Test
    void delete() {
        Actor seed = seeded.actor1();

        boolean deleted = actorDAO.delete(seed);

        assertThat(deleted, is(true));
        assertThrows(DatabaseException.class, () -> actorDAO.read(seed.getId()));
    }

    @Test
    void create_withNullActor_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> actorDAO.create(null));
        assertThat(ex.getMessage(), is("Actor is required")); // Adjust text to match your DAO implementation if different
    }

    @Test
    void getById_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> actorDAO.read(null));
        assertThat(ex.getMessage(), is("ID is required"));
    }

    @Test
    void getById_withMissingId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> actorDAO.read(999_999));
        assertThat(ex.getMessage(), is("Actor not found with id: 999999"));
    }

    @Test
    void update_withNullActor_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> actorDAO.update(null));
        assertThat(ex.getMessage(), is("Actor is required for update"));
    }

    @Test
    void update_withMissingId_throwsDatabaseException() {
        Actor actor = Actor.builder()
                .id(999_999)
                .name("not existing")
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> actorDAO.update(actor));
        assertThat(ex.getMessage(), is("Updating Actor failed"));
    }

    @Test
    void delete_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> actorDAO.delete(null));
        assertThat(ex.getMessage(), is("Actor is required for deletion"));
    }

    @Test
    void delete_withMissingId_throwsDatabaseException() {
        Actor actor = Actor.builder()
                .id(999_999)
                .name("not existing")
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> actorDAO.delete(actor));
        assertThat(ex.getMessage(), is("Delete Actor failed"));
    }
}