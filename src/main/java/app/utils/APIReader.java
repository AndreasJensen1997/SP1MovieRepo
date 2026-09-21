package app.utils;

import app.dto.Crew;
import app.dto.MovieDTO;
import app.dto.Result;
import app.services.MovieService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class APIReader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Integer> getAllIdsDanishMoviesLast15Years(){

        String baseUrl = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=da-DK&page=1&primary_release_date.gte=1980-01-01&sort_by=popularity.desc&vote_count.gte=2&with_original_language=da&api_key="+ System.getenv("API_KEY");
        String pageIdUrl = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=da-DK&page=$&primary_release_date.gte=1980-01-01&sort_by=popularity.desc&vote_count.gte=2&with_original_language=da&api_key="+ System.getenv("API_KEY");
        try {
            JsonNode node = objectMapper.readTree(new URI(baseUrl).toURL().openStream());
            JsonNode countNode = node.get("total_pages");
            Integer pageCount = objectMapper.convertValue(countNode, Integer.class);

            List<Integer> movieIds = new ArrayList<>();
            List<String> urls = new ArrayList<>();
            for (int i = 1; i <= pageCount; i++) {
                String url3 = pageIdUrl.replace("$", String.valueOf(i));
                urls.add(url3);
            }
            List<Callable<List<Integer>>> callables = new ArrayList<>();
            List<Future<List<Integer>>> futures = new ArrayList<>();
            for (String url : urls) {
                Callable<List<Integer>> callable = () -> {
                    JsonNode page = objectMapper.readTree(new URI(url).toURL().openStream());
                    JsonNode resultNode = page.get("results");
                    Result[] results = objectMapper.convertValue(resultNode, Result[].class);
                    List<Integer> ids = new ArrayList<>();
                    for (Result result : results) {
                        ids.add(result.id());
                    }
                    return ids;
                };
                callables.add(callable);
            }
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            for (Callable<List<Integer>> callable : callables) {
                Future<List<Integer>> future = executorService.submit(callable);
                futures.add(future);
            }

            for (Future<List<Integer>> future : futures) {
                List<Integer> ids = future.get();
                movieIds.addAll(ids);
            }
            return movieIds;

        } catch (IOException | URISyntaxException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MovieDTO> convertIdsToDTOs(List<Integer> ids){
        String baseUrl = "https://api.themoviedb.org/3/movie/$?append_to_response=credits&language=da-DK&api_key="+ System.getenv("API_KEY");
        List<MovieDTO> movieDTOS = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        for (Integer id : ids) {
            String url = baseUrl.replace("$", String.valueOf(id));
            urls.add(url);
        }
        List<Callable<MovieDTO>> callables = new ArrayList<>();
        List<Future<MovieDTO>> futures = new ArrayList<>();

        for (String url : urls) {
            Callable<MovieDTO> callable = () -> {
                try {
                    JsonNode node = objectMapper.readTree(new URI(url).toURL().openStream());
                    MovieDTO movieDTO = objectMapper.convertValue(node, MovieDTO.class);
                    return filterCrew(movieDTO);
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            };
            callables.add(callable);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for (Callable<MovieDTO> callable : callables) {
            Future<MovieDTO> future = executorService.submit(callable);
            futures.add(future);
        }

        for (Future<MovieDTO> future : futures) {
            try {
                MovieDTO movieDTO = future.get();
                movieDTOS.add(movieDTO);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return movieDTOS;
    }

    private MovieDTO filterCrew(MovieDTO movieDTO) {

        List<Crew> directors = movieDTO.credit().getCrew().stream()
                .filter(c -> c.job().equals("Director")).toList();
        movieDTO.credit().setCrew(directors);
        return movieDTO;
    }


}
