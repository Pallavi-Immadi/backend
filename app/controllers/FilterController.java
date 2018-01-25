package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Movie;
import models.User;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import javax.persistence.EntityManager;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Iterator;
import java.util.List;

import static play.db.jpa.JPA.em;

public class FilterController extends Controller {
    private JPAApi jpaApi;

    @Inject
    public FilterController(JPAApi jpaApi) {
        this.jpaApi=jpaApi;
    }


    @Transactional
    public Result findByTitle() {


        final JsonNode jsonNode = request().body().asJson();
        final String title = jsonNode.get("Title").asText();



        if (null == title) {
            return badRequest("Missing title");
        }


        TypedQuery<Movie> query = jpaApi.em().createQuery("select m from Movie m where title='" + title + "'", Movie.class);

        List<Movie> mov = query.getResultList();

        final JsonNode jsonNode1 = Json.toJson(mov);

        return ok(jsonNode1);

    }


    /*public Movie findByimdbID(String imdbID) {

        final Movie movie = jpaApi.em().find(Movie.class, imdbID);
        return movie;
    }
    */




    @Transactional
    public Result findByLanguage() {


        final JsonNode jsonNode = request().body().asJson();
         String language = jsonNode.get("language").asText();



        if (null ==language ) {
            return badRequest("missing language");
        }


        TypedQuery<Movie> query = jpaApi.em().createQuery("select m from Movie m where language LIKE      '" +"%"+ language + "%"+"'      ", Movie.class);

        List<Movie> mov = query.getResultList();

        final JsonNode jsonNode1 = Json.toJson(mov);

        return ok(jsonNode1);

    }


    @Transactional
    public Result findByGenre() {


        final JsonNode jsonNode = request().body().asJson();
        String genre = jsonNode.get("genre").asText();



        if (null ==genre ) {
            return badRequest("missing genre");
        }


        TypedQuery<Movie> query = jpaApi.em().createQuery("select m from Movie m where genre LIKE      '" +"%"+ genre + "%"+"'      ", Movie.class);

        List<Movie> mov = query.getResultList();

        final JsonNode jsonNode1 = Json.toJson(mov);

        return ok(jsonNode1);

    }


    public Result filter() {


        final JsonNode jsonNode = request().body().asJson();
        final Iterator<String> itr = jsonNode.fieldNames();

        while (itr.hasNext()) {

          String parameters= itr.next();

            System.out.print(parameters + "\n");

        }

        final JsonNode jsonNode1 = Json.toJson(itr);
        return ok(jsonNode1);


    }

    @Transactional
   public Result DynamicQuery(){

        return TODO;



      }

}
