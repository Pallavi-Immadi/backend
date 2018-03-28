package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import daos.MovieDao;
import daos.RatingsDao;
import daos.UserDao;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class RecommendationController extends Controller {

    private JPAApi jpaApi;
    private RatingsDao ratingsDao;
    private UserDao userDao;
    private MovieDao movieDao;

    @Inject
    public RecommendationController(JPAApi jpaApi, RatingsDao ratingsDao, UserDao userDao, MovieDao movieDao) {
        this.jpaApi = jpaApi;
        this.ratingsDao = ratingsDao;
        this.userDao = userDao;
        this.movieDao = movieDao;
    }

    @Transactional
    public Result generateRecommendations()
    {

        Map<Integer,HashMap<String,Double>> data=ratingsDao.getRatings();
        Map<String, Map<String, Double>> diffMatrix;
        Map<String, Map<String, Integer>> freqMatrix;
        diffMatrix = new HashMap<>();
        freqMatrix = new HashMap<>();
        // building Matrices
        for (Map<String, Double> user : data.values())
        {
            // then iterate through user data
            for (Map.Entry<String, Double> entry : user.entrySet())
            {
                String i1 = entry.getKey();
                double r1 = entry.getValue();

                if (!diffMatrix.containsKey(i1))
                {
                    diffMatrix.put(i1, new HashMap<String, Double>());
                    freqMatrix.put(i1, new HashMap<String, Integer>());
                }

                for (Map.Entry<String, Double> entry2 : user.entrySet())
                {
                    String i2 = entry2.getKey();
                    double r2 = entry2.getValue();

                    int cnt = 0;
                    if (freqMatrix.get(i1).containsKey(i2)) cnt = freqMatrix.get(i1).get(i2);
                    double diff = 0.0;
                    if (diffMatrix.get(i1).containsKey(i2)) diff = diffMatrix.get(i1).get(i2);
                    double new_diff = r1 - r2;

                    freqMatrix.get(i1).put(i2, cnt + 1);
                    diffMatrix.get(i1).put(i2, diff + new_diff);
                }
            }
        }
        for (String j : diffMatrix.keySet())
        {
            for (String i : diffMatrix.get(j).keySet())
            {
                Double oldvalue = diffMatrix.get(j).get(i);
                int count = freqMatrix.get(j).get(i).intValue();
                diffMatrix.get(j).put(i, oldvalue / count);
            }
        }






        final JsonNode json = Json.toJson(diffMatrix);
        return ok(json);

    }


}
