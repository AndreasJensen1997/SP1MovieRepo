import app.dto.MovieDTO;
import app.entities.Movie;
import app.services.MovieService;
import app.utils.APIReader;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        APIReader apiReader = new APIReader();
        List<Integer> ids = apiReader.getAllIdsDanishMoviesLast15Years();
        List<MovieDTO> movieDTOS = apiReader.convertIdsToDTOs(ids);
        System.out.println(movieDTOS.size());

        MovieService movieService = new MovieService();
        List<Movie> movies = movieService.persistEntities(movieDTOS);
        System.out.println(movies.getFirst().getId());
        System.out.println(movies.size());

    }
}
