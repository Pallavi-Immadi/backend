package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import daos.MovieDao;
import daos.ViewsDao;
import models.Movie;
import models.Views;
import play.Logger;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.ResultSet;
import java.util.*;

public class ViewsController extends Controller{

    private JPAApi jpaApi;
    private ViewsDao viewsDao;
    private MovieDao movieDao;

    @Inject
    public ViewsController (ViewsDao viewsDao,JPAApi jpaApi,MovieDao movieDao) {
        this.jpaApi=jpaApi;
        this.viewsDao = viewsDao;
        this.movieDao = movieDao;
    }


    /*
    @Transactional
    public Result updateViews() {


        final JsonNode jsonNode = request().body().asJson();
        final String imdbID = jsonNode.get("imdbID").asText();

        Views view=viewsDao.findById(imdbID);


        if (null == view) {
            Views newView = new Views();
            newView.setId(imdbID);
            newView.setImdbID(movieDao.findById(imdbID));
            newView.setViews(1);
            newView = viewsDao.persist(newView);

        }
        else {

            view.setViews(view.getViews()+1);
        }

        return ok("view updated");

    }
    */


    @Transactional
    public Result updateViews(String imdbID) {


       // final JsonNode jsonNode = request().body().asJson();
        //final String imdbID = jsonNode.get("imdbID").asText();

        Views view=viewsDao.findById(imdbID);


        if (null == view) {
            Views newView = new Views();
            newView.setId(imdbID);
            newView.setImdbID(movieDao.findById(imdbID));
            newView.setViews(1);
            newView = viewsDao.persist(newView);

        }
        else {

            view.setViews(view.getViews()+1);
        }

        return ok("view updated");

    }



   /* @Transactional
    public Result getMostPopularByGenre(){


         Map<String,Map> map = viewsDao.findViewsByGenre();
         Collection<Map> map1 = map.values();
         Map<String,Integer> map3=new TreeMap<String,Integer>(map1);



        final JsonNode jsonNode = Json.toJson(map3);

        return ok(jsonNode);


    }*/
}
