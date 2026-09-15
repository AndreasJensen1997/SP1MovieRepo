package app.utils;

import app.dto.Result;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class APIReader {
    private final ObjectMapper objectMapper = new ObjectMapper();


    public <T> T getWithJacksonGeneric(String url, Class<T> tClass){
        try {
            JsonNode node = objectMapper.readTree(new URI(url).toURL().openStream());
            return objectMapper.treeToValue(node, tClass);
        } catch (JacksonException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getAllIdsDanishMoviesLast15Years(){

        String url = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=da-DK&page=1&primary_release_date.gte=1980-01-01&sort_by=popularity.desc&vote_count.gte=2&with_original_language=da&api_key="+ System.getenv("API_KEY");
        String url2 = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=da-DK&page=$&primary_release_date.gte=1980-01-01&sort_by=popularity.desc&vote_count.gte=2&with_original_language=da&api_key="+ System.getenv("API_KEY");
        try {
            JsonNode node =objectMapper.readTree(new URI(url).toURL().openStream());
            JsonNode countNode = node.get("total_pages");
            Integer pageCount = objectMapper.convertValue(countNode, Integer.class);

            List<Integer> ids = new ArrayList<>();
            for (int i = 1; i <= pageCount; i++){
                String url3 = url2.replace("$", String.valueOf(i));
                JsonNode page = objectMapper.readTree(new URI(url3).toURL().openStream());
                JsonNode resultNode = page.get("results");
                Result[] results = objectMapper.convertValue(resultNode, Result[].class);

                for (Result result : results) {
                    ids.add(result.id());
                }
            }
            return ids;

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }



}
