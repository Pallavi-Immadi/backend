package controllers;

import ch.qos.logback.classic.ViewStatusMessagesServlet;
import com.fasterxml.jackson.databind.JsonNode;
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

    @Inject
    public HomepageController(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    @Transactional
    public Result mostPopular() {

        TypedQuery<Views> query = jpaApi.em().createQuery("select v from Views v ORDER BY views DESC ", Views.class);
        query.setMaxResults(15);
        List<Views> Result = query.getResultList();
        Collection<Movie> movieList= new ArrayList<>();
        for(Views v:Result)
        {
          movieList.add(v.getImdbID());
        }

        final JsonNode jsonNode = Json.toJson(movieList);
        return ok(jsonNode);


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


