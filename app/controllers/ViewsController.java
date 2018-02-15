package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import daos.MovieDao;
import daos.ViewsDao;
import models.Movie;
import models.Views;
import play.Logger;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.ResultSet;
import java.util.List;

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


    @Transactional
    public Result updateViews() {


        final JsonNode jsonNode = request().body().asJson();
        final String imdbID = jsonNode.get("imdbID").asText();


         String sql="select views from Views where imdbID="+movieDao.findById(imdbID)+";";
         Query query=jpaApi.em().createNativeQuery(sql);
         List<Views> result = query.getResultList();


        Views view=new Views();
        Movie movie=new Movie();

       //Integer count=query.getFirstResult();
         if( result.isEmpty())
         { //view.setId();
            view.setImdbID(movieDao.findById(imdbID));
            view.setViews(1);
         }
         else
         {
             Query query1=jpaApi.em().createNativeQuery("update Views set views=views+1 where imdbID='"+imdbID+"';");
         }

        view=viewsDao.persist(view);

      //  return created(user.getUname() + "This is your username");
        return ok("");

    }
}
