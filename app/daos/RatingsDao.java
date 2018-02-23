package daos;



import com.fasterxml.jackson.databind.JsonNode;
import models.Movie;
import models.Ratings;
import models.User;
import play.db.jpa.JPAApi;
import play.libs.Json;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RatingsDao {


    private JPAApi jpaApi;
    private MovieDao movieDao;



    @Inject
    public RatingsDao(JPAApi jpaApi,MovieDao movieDao) {
        this.jpaApi = jpaApi;
        this.movieDao=movieDao;
    }

    public Ratings persist(Ratings ratings) {

        jpaApi.em().persist(ratings);

        return ratings;
    }


 /*   public List findfrequentGenre(Integer userid){
        List<Movie> movies = findByuserID(userid);
        List genre;
        for (Movie movie: movies) {
           genre.add(movie.getGenre());
        }        return movies;
    }
*/

    public List<Movie> findByUserID(Integer userid) {


        Query query = jpaApi.em().createQuery("select r from Ratings r where userid='" +userid+ "'");

        List<Ratings> Result = query.getResultList();

        List<Movie> movieList=new ArrayList<>();
        for(Ratings ratings:Result)
        {
            movieList.add(movieDao.findById(ratings.getMovieId()));

        }

        return movieList;


   }


    public List<Ratings> findAll() {

        TypedQuery<Ratings> query = jpaApi.em().createQuery("SELECT r FROM Ratings r", Ratings.class);
        List<Ratings> ratings = query.getResultList();

        return ratings;
    }
}
