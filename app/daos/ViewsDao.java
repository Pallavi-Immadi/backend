package daos;


import com.fasterxml.jackson.databind.JsonNode;
import models.Movie;
import models.Views;
import play.db.jpa.JPAApi;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewsDao {

    private JPAApi jpaApi;
    private MovieDao movieDao;

    @Inject
    public ViewsDao(JPAApi jpaApi, MovieDao movieDao) {
        this.jpaApi = jpaApi;
        this.movieDao = movieDao;
    }

    public Views persist(Views views) {

        jpaApi.em().persist(views);

        return views;
    }


    public Views findById(String imdbID) {

        final Views views = jpaApi.em().find(Views.class, imdbID);
        return views;

    }


    public Map<String, Map> findViewsByGenre() {

        List<Views> views = findAll();

        Map<String, Map> map = new HashMap<>();
        List<String> genreList = new ArrayList<>();
        List<String> genreList1 = new ArrayList<>();


        for (Views view : views) {

          // genreList =  view.getImdbID().getGenre();
                String genre=view.getImdbID().getGenre();
                String[] genres=genre.split(", ");
                for(String gen:genres)
                {
                    genreList1.add(gen);
                }

                for (String genre1 : genreList1) {
                Map<String, Integer> map1 = new HashMap<>();
                if (map.containsKey(genre1)) {
                    map1 = map.get(genre1);
                    map1.put(view.getId(), view.getViews());
                    map.put(genre1, map1);
                }else {
                    map1.put(view.getId(), view.getViews());
                    map.put(genre1, map1);
                }
            }
        }
        return map;
    }

    public List<Views> findAll() {

        TypedQuery<Views> query = jpaApi.em().createQuery("SELECT v FROM Views v", Views.class);
        List<Views> views = query.getResultList();

        return views;
    }

    public Views deleteViews(String imdbID) {

        final Views views = findById(imdbID);

        if (null == views) {
            return null;
        }

        jpaApi.em().remove(views);

        return views;
    }







}
