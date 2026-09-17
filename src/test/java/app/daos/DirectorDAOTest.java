package app.daos;

import app.config.HibernateTestConfig;
import app.entities.Director;
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
class DirectorDAOTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private DirectorDAO directorDAO;
    private TestPopulator.SeededData seeded;

    @BeforeEach
    void setUp() {
        seeded = TestPopulator.populate(emf);
    }

    @BeforeAll
    void setUpAll() {
        directorDAO = new DirectorDAO(emf);
    }

    @Test
    void create() {
        Director newDirector = Director.builder()
                .name("Quentin Tarantino")
                .pictureUrl("tarantino.url")
                .build();

        Director createdDirector = directorDAO.create(newDirector);
        assertThat(createdDirector.getId(), notNullValue());
        assertThat(createdDirector.getName(), is(newDirector.getName()));
        assertThat(createdDirector.getPictureUrl(), is(newDirector.getPictureUrl()));
    }

    @Test
    void read() {
        Director director = seeded.director1();
        Director fetchedDirector = directorDAO.read(director.getId());
        assertThat(fetchedDirector, notNullValue());
        assertThat(fetchedDirector.getId(), is(director.getId()));
        assertThat(fetchedDirector.getName(), is(director.getName()));
    }

    @Test
    void readAll() {
        Set<Director> directors = Set.of(seeded.director1(), seeded.director2());
        Set<Director> fetchedDirectors = directorDAO.readAll();
        assertThat(fetchedDirectors, notNullValue());
        assertThat(fetchedDirectors.size(), is(directors.size()));
    }

    @Test
    void update() {
        Director seed = seeded.director1();
        Director updated = Director.builder()
                .id(seed.getId())
                .name("Martin Scorsese")
                .pictureUrl("scorsese.url").build();

        Director result = directorDAO.update(updated);

        assertThat(result.getId(), is(seed.getId()));
        assertThat(result.getName(), is("Martin Scorsese"));
        assertThat(result.getPictureUrl(), is("scorsese.url"));
    }

    @Test
    void delete() {
        Director seed = seeded.director1();

        boolean deleted = directorDAO.delete(seed);

        assertThat(deleted, is(true));
        assertThrows(DatabaseException.class, () -> directorDAO.read(seed.getId()));
    }

    @Test
    void create_withNullDirector_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> directorDAO.create(null));
        assertThat(ex.getMessage(), is("Director is required"));
    }

    @Test
    void getById_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> directorDAO.read(null));
        assertThat(ex.getMessage(), is("ID is required"));
    }

    @Test
    void getById_withMissingId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> directorDAO.read(999_999));
        assertThat(ex.getMessage(), is("Director not found with id: 999999"));
    }

    @Test
    void update_withNullDirector_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> directorDAO.update(null));
        assertThat(ex.getMessage(), is("Director is required for update"));
    }

    @Test
    void update_withMissingId_throwsDatabaseException() {
        Director director = Director.builder()
                .id(999_999)
                .name("not existing")
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> directorDAO.update(director));
        assertThat(ex.getMessage(), is("Updating Director failed"));
    }

    @Test
    void delete_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> directorDAO.delete(null));
        assertThat(ex.getMessage(), is("Director is required for deletion"));
    }

    @Test
    void delete_withMissingId_throwsDatabaseException() {
        Director director = Director.builder()
                .id(999_999)
                .name("not existing")
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> directorDAO.delete(director));
        assertThat(ex.getMessage(), is("Delete Director failed"));
    }
}