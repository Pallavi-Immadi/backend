package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.security.Authenticator;
import controllers.security.IsAdmin;
import daos.MovieDao;
import daos.ViewsDao;
import models.Movie;
import models.Views;
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
import java.util.List;

public class MovieController extends Controller {

    private MovieDao movieDao;
    private ViewsDao viewsDao;

    @Inject
    public MovieController (MovieDao movieDao,ViewsDao viewsDao) {
        this.movieDao = movieDao;
        this.viewsDao = viewsDao;

    }


    @Transactional
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

    public Result deleteMovie(String imdbID){

        Views views  = viewsDao.deleteViews(imdbID);

        final Movie movie = movieDao.deleteMovie(imdbID);

        if(null == movie){
            return notFound("movie with the following imdbID not found"+imdbID);
        }

        return noContent();
    }

    @Transactional
    public Result updateMovie(String imdbID){
        return TODO;

    }

    @Transactional
    public Result getAllMovies(){

        final List<Movie> movies = movieDao.findAll();

        final JsonNode jsonNode = Json.toJson(movies);

        return ok(jsonNode);

    }

    @Transactional
    public Result getMovieByID(String imdbID){

        Movie movie=movieDao.findById(imdbID);

        final JsonNode json = Json.toJson(movie);
        return ok(json);
    }




}