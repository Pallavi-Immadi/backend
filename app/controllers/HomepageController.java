package controllers;

import ch.qos.logback.classic.ViewStatusMessagesServlet;
import com.fasterxml.jackson.databind.JsonNode;
import daos.MovieDao;
import daos.RatingsDao;
import daos.UserDao;
import daos.ViewsDao;
import models.Movie;
import models.User;
import models.Views;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HomepageController extends Controller {
    private JPAApi jpaApi;
    private RatingsDao ratingsDao;
    private ViewsDao viewsDao;
    private MovieDao movieDao;

    @Inject
    public HomepageController(JPAApi jpaApi, RatingsDao ratingsDao,ViewsDao viewsDao, MovieDao movieDao) {
        this.jpaApi = jpaApi;
        this.ratingsDao = ratingsDao;
        this.viewsDao=viewsDao;
        this.movieDao = movieDao;
    }


    @Transactional
    public Result mostPopular() {

        List<Movie> movieList = viewsDao.getMostPopular();
        final JsonNode jsonNode = Json.toJson(movieList);
        return ok(jsonNode);

    }


    @Transactional
    public Result latestMovies() {

        List<Movie> Result = movieDao.findLatestMovies();
        final JsonNode jsonNode = Json.toJson(Result);
        return ok(jsonNode);
    }


    @Transactional
    public Result topRated(){


        List<String> movies = ratingsDao.getTopRated();
        List<Movie> result = new ArrayList<>();
        List<String> finalList=new ArrayList<>();
        int i=0;
        for(String movie:movies)
        {
            if(i<10)
            finalList.add(movie);
            i++;
        }

        for(String mov:finalList)
        {
            result.add(movieDao.findById(mov));
        }

        final JsonNode jsonNode = Json.toJson(result);
        return ok(jsonNode);
    }

}


