package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Movie;
import models.User;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.List;

public class HomepageController extends Controller {
    private JPAApi jpaApi;

    @Inject
    public HomepageController(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    @Transactional
    public Result mostPopular() {


        return TODO;


    }


    @Transactional
    public Result latestMovies() {

       TypedQuery<Movie> query = jpaApi.em().createQuery("select m from Movie m ORDER BY date DESC ", Movie.class);
        query.setMaxResults(15);
        List<Movie> Result = query.getResultList();
        final JsonNode jsonNode = Json.toJson(Result);
        return ok(jsonNode);
    }


    @Transactional
    public Result Reccomendations(){





        return TODO;
    }

}


