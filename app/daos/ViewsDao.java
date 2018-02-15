package daos;


import models.Views;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.List;

public class ViewsDao {

    private JPAApi jpaApi;

    @Inject
    public ViewsDao(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    public Views persist(Views views) {

        jpaApi.em().persist(views);

        return views;
    }



    public Views findById(String imdbID) {

        final Views views = jpaApi.em().find(Views.class, imdbID);
        return views;

    }


    public List<Views> findAll() {

        TypedQuery<Views> query = jpaApi.em().createQuery("SELECT v FROM Views v", Views.class);
        List<Views> views = query.getResultList();

        return views;
    }

}
