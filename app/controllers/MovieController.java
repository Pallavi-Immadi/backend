package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.Authenticator;
import controllers.security.IsAdmin;
import daos.MovieDao;
import daos.RatingsDao;
import daos.ViewsDao;
import models.Movie;
import models.User;
import models.Views;
import play.Logger;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.Results;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.awt.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MovieController extends Controller {

    private MovieDao movieDao;
    private ViewsDao viewsDao;
    private RatingsDao ratingsDao;
    private  ViewsController viewsController;

    private List<Movie> topRatedMovies = new LinkedList<>();

    @Inject
    public MovieController (MovieDao movieDao, ViewsDao viewsDao,ViewsController viewsController,RatingsDao ratingsDao) {
        this.movieDao = movieDao;
        this.viewsDao = viewsDao;
        this.viewsController = viewsController;
        this.ratingsDao = ratingsDao;

    }


    @Transactional
    @Authenticator
    @IsAdmin

    public Result createMovie() {

        SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd");

        final JsonNode jsonNode = request().body().asJson();
        final String imdbID = jsonNode.get("imdbID").asText();
        final String title = jsonNode.get("Title").asText();
        final String date=jsonNode.get("Date").asText();
        final String certification=jsonNode.get("Rated").asText();
        final String runtime=jsonNode.get("Runtime").asText();
        final String genre = jsonNode.get("Genre").asText();
        final String director = jsonNode.get("Director").asText();
        final String actors = jsonNode.get("Actors").asText();
        final String plot = jsonNode.get("Plot").asText();
        final String language = jsonNode.get("Language").asText();
        final String poster = jsonNode.get("Poster").asText();


        Movie movie = new Movie();
        movie.setImdbID(imdbID);
        movie.setTitle(title);
        movie.setDate(formatter1.parse(date,new ParsePosition(0)));
        movie.setCertification(certification);
        movie.setRuntime(runtime);
        movie.setGenre(genre);
        movie.setDirector(director);
        movie.setActors(actors);
        movie.setPlot(plot);
        movie.setLanguage(language);
        movie.setPoster(poster);


        movie=movieDao.persist(movie);

        System.out.println(formatter1.parse(date,new ParsePosition(0)));

        return created("Movie created");

    }


    @Transactional
    public Result updateMovie(String imdbID){
        return TODO;

    }

    @Transactional
   //@Authenticator //is working
    public Result getAllMovies(){

        final List<Movie> movies = movieDao.findAll();

        final JsonNode jsonNode = Json.toJson(movies);

        return ok(jsonNode);

    }

    @Transactional
    @Authenticator
    @IsAdmin

    public Result deleteMovie(){

        final JsonNode jsonNode = request().body().asJson();
        if(jsonNode == null){
            return  badRequest("cannot read from json as it is null");
        }

        String imdbID = jsonNode.get("imdbID").asText();
        if(imdbID == null){
            return badRequest("cannot delete movie without imdbID,Please enter the imdbID");
        }



        Views views  = viewsDao.deleteViews(imdbID);
        Boolean ratings = ratingsDao.deleteRatings(imdbID);
        final Movie movie = movieDao.deleteMovie(imdbID);

        if(null == movie){
            return notFound("movie with the following imdbID not found"+imdbID);
        }

        return noContent();
    }



    @Transactional
  //  @Authenticator// authenticator is working
    public Result getMovieByID() {

        Float rating = null;
        final JsonNode jsonNode = request().body().asJson();
        final String imdbID = jsonNode.get("imdbID").asText();
        Logger.debug("" + imdbID);

        if(null == imdbID){
            return noContent();
        }

        final User user = (User) ctx().args.get("user");
        viewsController.updateViews(imdbID);

        rating = ratingsDao.getRatingforSingleMovie(imdbID,user.getId());
        Movie movie=movieDao.findById(imdbID);

        JsonNode json = Json.toJson(movie);
        ((ObjectNode)json).put("userRating",rating);
        return ok(json);
    }




}