package daos;


import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Pair;
import models.Movie;
import models.Ratings;
import models.User;
import play.Logger;
import play.db.jpa.JPAApi;
import play.libs.Json;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.security.KeyStore;
import java.util.*;
import java.util.stream.Collectors;

public class RatingsDao {


    private JPAApi jpaApi;
    private MovieDao movieDao;
    private UserDao userDao;

    @Inject
    public RatingsDao(JPAApi jpaApi,MovieDao movieDao,UserDao userDao) {
        this.jpaApi = jpaApi;
        this.movieDao=movieDao;
        this.userDao=userDao;
    }

    public Ratings persist(Ratings ratings) {

        jpaApi.em().persist(ratings);

        return ratings;
    }



    public List<Movie> findByUserID(Integer userid) {


        TypedQuery<Ratings> query = jpaApi.em().createQuery("select r from Ratings r where userid='" +userid+ "'",Ratings.class);

        List<Ratings> Result = query.getResultList();

        List<Movie> movieList=new ArrayList<>();
        for(Ratings ratings:Result)
        {
            movieList.add(movieDao.findById(ratings.getMovieId()));

        }

        return movieList;


    }

    public Map<Integer,HashMap<String,Double>> getRatings(){

        Query query=jpaApi.em().createQuery("select id from User");
        List<Integer> ids=query.getResultList();
        Map<Integer,HashMap<String,Double>> finalMap=new HashMap<>();

        for(Integer id:ids)
        {

            TypedQuery<Ratings> query1=jpaApi.em().createQuery("select r from Ratings r where userId="+id,Ratings.class);
            List<Ratings> ratings=query1.getResultList();
            HashMap<String,Double> map=new HashMap<>();
            for(Ratings rating:ratings){

                map.put(rating.getMovieId(),rating.getRating().doubleValue());
                finalMap.put(id,map);
            }


        }
        return finalMap;

    }

    public Map<String,ArrayList<String>> findAverageRatingsByGenre() {

        List<Ratings> ratings = findAll();
        Map<String,ArrayList<String>> finalList=new HashMap<>();
        Map<String, ArrayList<Pair>> map = new HashMap<>();
        List<String> genreList = new ArrayList<>();


        for (Ratings rating : ratings) {

            List <String> genreList1=movieDao.findGenre(rating.getMovieId());

            for (String genre1 : genreList1) {
                Pair<String,Float> movieRatingPair=new Pair<String,Float>("",null);
                ArrayList<Pair> ratingList=new ArrayList<>();
                if (finalList.containsKey(genre1)&&map.containsKey(genre1)) {
                    ratingList = map.get(genre1);
                    ratingList.add(new Pair(rating.getMovieId(), getAverageRating(rating.getMovieId())));
                    LinkedHashSet<Pair> lhs = new LinkedHashSet<Pair>();
                    lhs.addAll(ratingList);
                    ratingList.clear();
                    ratingList.addAll(lhs);
                    ratingList.sort(new Comparator<Pair>() {
                        @Override
                        public int compare(Pair o1, Pair o2) {
                            if (o1.getValue().hashCode()> o2.getValue().hashCode()) {
                                return -1;
                            } else if (o1.getValue().equals(o2.getValue())) {

                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    });
                    ArrayList<String> imdbList = new ArrayList<>();
                    int i=0;
                    for(Pair<String,Float> temp:ratingList)
                    {
                        if(i<5)
                        {
                            imdbList.add(temp.getKey());
                        }
                        i++;
                    }
                    finalList.put(genre1, imdbList);
                    map.put(genre1,ratingList);

                }else {
                    ratingList.add(new Pair(rating.getMovieId(),getAverageRating(rating.getMovieId())));
                    ArrayList<String> imdbList = new ArrayList();
                    int i=0;
                    for(Pair<String,Float> temp:ratingList)
                    {
                        if(i<5)
                        {
                            imdbList.add(temp.getKey());
                        }
                        i++;
                    }
                    map.put(genre1,ratingList);
                    finalList.put(genre1, imdbList);

                }
            }
        }

        return finalList;
    }




    public List<Movie> findPositiveRatingsByUserID(Integer userid) {


        Query query = jpaApi.em().createQuery("select r from Ratings r where userid='" +userid+ "'and rating>=3");

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



    public float getAverageRating(String imdbID)
    {
        Query query = jpaApi.em().createQuery("select rating from Ratings r where movieId='"+imdbID+"'");
        List<Float> ratings = query.getResultList();
        float total = 0;
        for(float rating:ratings){

            total = total+rating;
        }
        float averageRating = total/ratings.size();

        return averageRating;
    }

    public float getRatingforSingleMovie(String imdbID,Integer userId)
    {
        Query query = jpaApi.em().createQuery("select rating from Ratings r where movieId='"+imdbID+"'and userId="+userId+"");
        List<Float> rating = query.getResultList();
        float tempRating=0;
        if(!rating.isEmpty())
            tempRating=rating.get(0);
        return tempRating;

    }

    public List<String> getTopRated()
    {
        List<Ratings> ratings = findAll();
        List<String> movies=new ArrayList<>();
        Map<String,Float> map=new HashMap<>();
        for(Ratings r:ratings)
        {
            map.put(r.getMovieId(),getAverageRating(r.getMovieId()));

        }
        List<Map.Entry<String, Float>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        Map<String, Float> result = new LinkedHashMap<>();
        for (Map.Entry<String, Float> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        movies.addAll(result.keySet());

        Logger.debug(result+"");
        return movies;



    }

    public List<String> findRatingsByUserID(Integer userid) {

        Query query = jpaApi.em().createQuery("select r from Ratings r where userid=" +userid);

        List<Ratings> Result = query.getResultList();

        List<String> movieList=new ArrayList<>();
        for(Ratings ratings:Result)
        {
            movieList.add(ratings.getMovieId());

        }

        return movieList;

    }

    public boolean findUserId(Integer userId){

        Boolean result;

        TypedQuery<Ratings> query = jpaApi.em().createQuery("SELECT r FROM Ratings r where userId="+userId, Ratings.class);
        List<Ratings> ratings = query.getResultList();

        if(ratings.isEmpty())
            result=true;
        else
            result=false;

        return result;

    }

    public List<Ratings> findByMovieId(String imdbID) {


        Query query = jpaApi.em().createQuery("select r from Ratings r where movieId='"+imdbID+"'");
        List<Ratings> ratings = query.getResultList();


        return ratings;

    }


    public Boolean deleteRatings(String imdbID) {

        final List<Ratings> ratings  = findByMovieId(imdbID);

        if (null == ratings) {
            return null;
        }


        for (Ratings r:ratings) {

            jpaApi.em().remove(r);
        }

        return true;
    }




}
