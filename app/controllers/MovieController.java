package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import daos.MovieDao;
import models.Movie;
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
import java.util.List;

public class MovieController extends Controller {

    private MovieDao movieDao;

    @Inject
    public MovieController (MovieDao movieDao) {
        this.movieDao = movieDao;
    }


    @Transactional
    public Result createMovie() {

        final JsonNode jsonNode = request().body().asJson();
        final String imdbID = jsonNode.get("imdbID").asText();
        final String title = jsonNode.get("Title").asText();
        final Integer year = jsonNode.get("Year").asInt();
        final String certification=jsonNode.get("Rated").asText();
        final String runtime=jsonNode.get("Runtime").asText();
        final String genre = jsonNode.get("Genre").asText();
        final String director = jsonNode.get("Director").asText();
        final String actors = jsonNode.get("Actors").asText();
        final String plot = jsonNode.get("Plot").asText();
        final String language = jsonNode.get("Language").asText();
        final String poster = jsonNode.get("Poster").asText();






        /*if (null == username) {
            return badRequest("Missing user name");
        }
        if (null == password) {
            return badRequest("Missing password");
        }
        if (null == role) {
            return badRequest("Missing role");
        }
        // if (null == phone) {
        //return badRequest("Missing contact number");
        // }
        if (null == proname) {
            return badRequest("Missing profile name");
        }*/



        Movie movie = new Movie();
        movie.setImdbID(imdbID);
        movie.setTitle(title);
        movie.setYear(year);
        movie.setCertification(certification);
        movie.setRuntime(runtime);
        movie.setGenre(genre);
        movie.setDirector(director);
        movie.setActors(actors);
        movie.setPlot(plot);
        movie.setLanguage(language);
        movie.setPoster(poster);


        movie=movieDao.persist(movie);

        return created("Movie created");

    }

    @Transactional
    public Result deleteMovie(){

        final JsonNode jsonNode = request().body().asJson();
        final String imdbID = jsonNode.get("imdbID").asText();

        if (null == imdbID) {
            return badRequest("Missing imdbID");
        }

        final Movie movie = movieDao.deleteMovie(imdbID);

        if(null==movie){
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

}