package app.services;

import app.config.HibernateConfig;
import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.MovieDAO;
import app.dto.Cast;
import app.dto.Crew;
import app.dto.GenreDTO;
import app.dto.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Movie;
import app.enums.Genre;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MovieService {
    private EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private MovieDAO movieDAO = new MovieDAO(emf);
    private ActorDAO actorDAO = new ActorDAO(emf);
    private DirectorDAO directorDAO = new DirectorDAO(emf);


    public List<Movie> persistEntities(List<MovieDTO> movieDTOS) {

        List<Movie> movies = new ArrayList<>();
        for (MovieDTO movieDTO : movieDTOS) {
            Movie movie = convertMovieDtoToMovie(movieDTO);
            movies.add(movie);
        }

        List<Movie> moviesCreated = new ArrayList<>();
        for (Movie movie : movies) {
            Movie movieCreated = movieDAO.create(movie);
            moviesCreated.add(movieCreated);
        }
        return moviesCreated;
    }

    private Movie convertMovieDtoToMovie(MovieDTO movieDTO) {

        Movie movie = Movie.builder()
                .language(movieDTO.language())
                .overview(movieDTO.overview())
                .title(movieDTO.title())
                .releaseDate(LocalDate.parse(movieDTO.releaseDate()))
                .rating(movieDTO.rating())
                .genres(convertMovieDtosToGenres(movieDTO))
                .posterURL(movieDTO.posterPath())
                .build();

        movie.addActors(convertMovieDtosToActors(movieDTO));
        movie.addDirectors(convertMovieDtosToDirectors(movieDTO));
        return movie;
    }


    private List<Genre> convertMovieDtosToGenres(MovieDTO movieDTO) {

        List<Genre> genres = new ArrayList<>();

        for (GenreDTO genre : movieDTO.genres()) {
            switch (genre.toString().toLowerCase()) {
                case "action" -> genres.add(Genre.ACTION);
                case "adventure" -> genres.add(Genre.ADVENTURE);
                case "animation" -> genres.add(Genre.ANIMATION);
                case "comedy" -> genres.add(Genre.COMEDY);
                case "crime" -> genres.add(Genre.CRIME);
                case "documentary" -> genres.add(Genre.DOCUMENTARY);
                case "drama" -> genres.add(Genre.DRAMA);
                case "family" -> genres.add(Genre.FAMILY);
                case "fantasy" -> genres.add(Genre.FANTASY);
                case "history" -> genres.add(Genre.HISTORY);
                case "horror" -> genres.add(Genre.HORROR);
                case "music" -> genres.add(Genre.MUSIC);
                case "mystery" -> genres.add(Genre.MYSTERY);
                case "romance" -> genres.add(Genre.ROMANCE);
                case "science_fiction", "sci-fi" -> genres.add(Genre.SCIENCE_FICTION);
                case "tv_movie" -> genres.add(Genre.TV_MOVIE);
                case "thriller" -> genres.add(Genre.THRILLER);
                case "war" -> genres.add(Genre.WAR);
                case "western" -> genres.add(Genre.WESTERN);
                default -> genres.add(Genre.OTHER);
            }
        }
        return genres;
    }

    private List<Actor> convertMovieDtosToActors(MovieDTO movieDTO) {

        List<Actor> actors = new ArrayList<>();
        List<Cast> casts = movieDTO.credit().getCast();
        for (Cast cast : casts) {
            Actor actor = Actor.builder()
                    .name(cast.originalName())
                    .character(cast.character())
                    .pictureURL(cast.imageUrl())
                    .build();


            Set<Actor> allActorsInDb = actorDAO.readAll();
            for (Actor actor1 : allActorsInDb) {
                if (actor.getName().equalsIgnoreCase(actor1.getName())) {
                    Actor fetchedActor = actorDAO.read(actor1.getId());
                    actors.add(fetchedActor);
                } else {
                    Actor actor2 = actorDAO.create(actor);
                    actors.add(actor2);
                }
            }
        }
        return actors;

    }

    private List<Director> convertMovieDtosToDirectors(MovieDTO movieDTO) {

        List<Director> directors = new ArrayList<>();
        List<Crew> crewList = movieDTO.credit().getCrew();
        for (Crew crew : crewList) {
            Director director = Director.builder()
                    .name(crew.originalName())
                    .pictureUrl(crew.imageUrl())
                    .build();

            Set<Director> allDirectorsInDb = directorDAO.readAll();
            for (Director director1 : allDirectorsInDb) {
                if (director.getName().equalsIgnoreCase(director1.getName())) {
                    Director fetchedDirector = directorDAO.read(director1.getId());
                    directors.add(fetchedDirector);
                } else {
                    Director director2 = directorDAO.create(director);
                    directors.add(director2);
                }
            }
        }
        return directors;
    }
}
