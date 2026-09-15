import app.utils.APIReader;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        APIReader apiReader = new APIReader();
        List<Integer> ids = apiReader.getAllIdsDanishMoviesLast15Years();
        System.out.println(ids.size());


    }
}
