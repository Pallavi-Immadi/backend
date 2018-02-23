package daos;




import models.Movie;
import models.Views;
import play.db.jpa.JPAApi;

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
    public ViewsDao(JPAApi jpaApi,MovieDao movieDao) {
        this.jpaApi = jpaApi;
        this.movieDao=movieDao;
    }

    public Views persist(Views views) {

        jpaApi.em().persist(views);

        return views;
    }



    public Views findById(String imdbID) {

        final Views views = jpaApi.em().find(Views.class, imdbID);
        return views;

    }


    public Map<String,Map> findViewsByGenre(){

        List<Views> views = findAll();

        Map<String,Map> map= new HashMap<>();
        List<String> genreList=new ArrayList<>();
        for(Views view:views) {
            genreList = movieDao.findGenre(view.getId());
            for (String genre : genreList) {
                Map<String, Integer> map1 = new HashMap<>();
                if (map.containsKey(genre)) {
                    Map<String, Integer> map2 = map.get(genre);
                    map2.put(view.getId(), view.getViews());
                    map.put(genre, map2);
                } else {
                    map1.put(view.getId(), view.getViews());
                    map.put(genre, map1);
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

        final Views views=findById(imdbID);

        if (null == views) {
            return null;
        }

        jpaApi.em().remove(views);

        return views;
    }

}
