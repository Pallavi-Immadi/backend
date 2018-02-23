package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import daos.MovieDao;
import daos.RatingsDao;
import daos.UserDao;
import io.jsonwebtoken.lang.Strings;
import models.Movie;
import models.Ratings;
import models.User;
import models.Views;
import play.Logger;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.Query;
import java.util.*;

public class RatingsController extends Controller {

    private JPAApi jpaApi;
    private RatingsDao ratingsDao;
    private UserDao userDao;
    private MovieDao movieDao;

    @Inject
    public RatingsController(JPAApi jpaApi, RatingsDao ratingsDao, UserDao userDao, MovieDao movieDao) {
        this.jpaApi = jpaApi;
        this.ratingsDao = ratingsDao;
        this.userDao = userDao;
        this.movieDao = movieDao;
    }

    @Transactional
    public Result insertRatings() {

        final JsonNode jsonNode = request().body().asJson();
        final String token = jsonNode.get("token").asText();
        final String imdbID = jsonNode.get("imdbID").asText();
        final Integer rating = Integer.parseInt(jsonNode.get("rating").asText());
        User user = userDao.findByToken(token);
        Ratings ratings = new Ratings();
        ratings.setUserId(user.getId());
        ratings.setMovieId(imdbID);
        ratings.setRating(rating);

        ratingsDao.persist(ratings);
        return ok("rating added");

    }

    @Transactional
    public Result findPersonalizedRecommendations(String token){


        User user = userDao.findByToken(token);
        List<Movie> movieList = ratingsDao.findByUserID(user.getId());

        List<String> genreList = new ArrayList<>();

        for(Movie movie : movieList)
        {
            String genre=movie.getGenre();
            String[] genres=genre.split(", ");
            for(String gen:genres)
            {
                genreList.add(gen);
            }

        }

        Logger.debug(genreList+"");
        Map<String, Integer> map = new HashMap<>();

        for(String genre:genreList){

            if(map.containsKey(genre)){
                Integer count=map.get(genre);
                map.put(genre,count+1);
            }
            else{
                map.put(genre,1);
            }
        }

        final JsonNode json = Json.toJson(map);
        return ok(json);



    }


}
