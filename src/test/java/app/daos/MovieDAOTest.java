package app.daos;

import app.config.HibernateTestConfig;
import app.entities.Movie;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.utils.TestPopulator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MovieDAOTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private MovieDAO movieDAO;
    private TestPopulator.SeededData seeded;

    @BeforeEach
    void setUp() {
        seeded = TestPopulator.populate(emf);

    }

    @BeforeAll
    void setUpAll() {
        movieDAO = new MovieDAO(emf);
    }

    @Test
    void create() {
        Movie newMovie = Movie.builder()
                .title("Yankee Doodle Dandy")
                .releaseDate(LocalDate.of(1942, 1, 1))
                .overview("The greatest movie of all time")
                .rating(10)
                .language("Not Danish")
                .posterURL("doodlePoster.url")
                .build();

        newMovie.addDirectors(List.of(seeded.director1()));
        newMovie.addActors(List.of(seeded.actor1(), seeded.actor2()));

        Movie createdMovie = movieDAO.create(newMovie);
        assertThat(createdMovie.getId(), notNullValue());
        assertThat(createdMovie.getTitle(), is(newMovie.getTitle()));
    }

    @Test
    void read() {
        Movie movie = seeded.movie1();
        Movie fetchedMovie = movieDAO.read(movie.getId());
        assertThat(fetchedMovie, notNullValue());
        assertThat(fetchedMovie.getId(), is(movie.getId()));
    }

    @Test
    void readAll() {
        Set<Movie> movies = Set.of(seeded.movie1(), seeded.movie2());
        Set<Movie> fetchedMovies = movieDAO.readAll();
        assertThat(fetchedMovies, notNullValue());
        assertThat(fetchedMovies.size(), is(movies.size()));
    }

    @Test
    void update() {
        Movie seed = seeded.movie1();

        Movie updated = Movie.builder()
                .id(seed.getId())
                .title("Yonkee dadle Doondy 2")
                .overview("The cheap knock off")
                .rating(2.0)
                .language("English")
                .posterURL("updated doodlePoster.url")
                .build();

        Movie result = movieDAO.update(updated);

        assertThat(result.getId(), is(seed.getId()));
        assertThat(result.getTitle(), is("Yonkee dadle Doondy 2"));
        assertThat(result.getOverview(), is("The cheap knock off"));
        assertThat(result.getRating(), is(2.0));
        assertThat(result.getLanguage(), is("English"));
        assertThat(result.getPosterURL(), is("updated doodlePoster.url"));
    }

    @Test
    void delete() {
        Movie seed = seeded.movie1();

        boolean deleted = movieDAO.delete(seed);

        assertThat(deleted, is(true));
        assertThrows(DatabaseException.class, () -> movieDAO.read(seed.getId()));
    }

    @Test
    void create_withNullAdvert_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> movieDAO.create(null));
        assertThat(ex.getMessage(), is("Movie is required"));
    }

    @Test
    void getById_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> movieDAO.read(null));
        assertThat(ex.getMessage(), is("ID is required"));
    }

    @Test
    void getById_withMissingId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> movieDAO.read(999_999));
        assertThat(ex.getMessage(), is("Movie not found with id: 999999"));
    }

    @Test
    void update_withNullAdvert_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> movieDAO.update(null));
        assertThat(ex.getMessage(), is("Movie is required for update"));
    }

    @Test
    void update_withMissingId_throwsApiException() {
        Movie movie = Movie.builder()
                .id(999_999)
                .title("not existing")
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> movieDAO.update(movie));
        assertThat(ex.getMessage(), is("Updating Movie failed"));
    }

    @Test
    void delete_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> movieDAO.delete(null));
        assertThat(ex.getMessage(), is("Movie is required for deletion"));
    }

    @Test
    void delete_withMissingId_throwsDatabaseException() {
        Movie movie = Movie.builder()
                .id(999_999)
                .title("not existing")
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> movieDAO.delete(movie));
        assertThat(ex.getMessage(), is("Delete Movie failed"));
    }
}