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
    return TODO;


        //final JsonNode json = Json.toJson(map);
        //return ok(json);

    }

}
