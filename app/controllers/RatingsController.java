package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.security.Authenticator;
import daos.MovieDao;
import daos.RatingsDao;
import daos.UserDao;
import io.jsonwebtoken.lang.Strings;
import javafx.util.Pair;
import models.Movie;
import models.Ratings;
import models.User;
import models.Views;
import play.Logger;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.libs.Time;
import play.mvc.Controller;
import play.mvc.Result;


import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

public class RatingsController extends Controller {

    private JPAApi jpaApi;
    private RatingsDao ratingsDao;
    private UserDao userDao;
    private MovieDao movieDao;
    private List<Movie> movieList = new LinkedList<>();

    Date lastRefresh = new Date();
    private static final long cachingDuration = 10 * 1000;
    Date nextRefresh = new Date(lastRefresh.getTime() + cachingDuration);


    @Inject
    public RatingsController(JPAApi jpaApi, RatingsDao ratingsDao, UserDao userDao,MovieDao movieDao) {
        this.jpaApi = jpaApi;
        this.ratingsDao = ratingsDao;
        this.userDao = userDao;
        this.movieDao = movieDao;

    }

    @Transactional
    @Authenticator
    public Result insertRatings() {

        final JsonNode jsonNode = request().body().asJson();
        //final String token = jsonNode.get("token").asText();
        final String imdbID = jsonNode.get("imdbID").asText();
        final Float rating = Float.parseFloat(jsonNode.get("rating").asText());
        User user = (User) ctx().args.get("user");
        Ratings ratings = new Ratings();
        ratings.setUserId(user.getId());
        ratings.setMovieId(imdbID);
        ratings.setRating(rating);

        ratingsDao.persist(ratings);
        return ok("rating added");

    }
    @Transactional
    @Authenticator
    public Result findPersonalizedRecommendations(){

        Calendar calendar = Calendar.getInstance();
        Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());

        User user = (User) ctx().args.get("user");
        if(movieList.isEmpty() || currentTime.after(nextRefresh)) {
            movieList.clear();
            movieList = ratingsDao.findPositiveRatingsByUserID(user.getId());
            lastRefresh.setTime(calendar.getTimeInMillis());
        }

        List<String> genreList = new ArrayList<>();

        for(Movie movie : movieList)
        {
            String genre=movie.getGenre();
            String[] genres=genre.split(", ");
            for(String gen:genres)
            {
                genreList.add(gen);
            }

        }

        Map<String, Integer> map = new HashMap<>();

        for(String genre:genreList){

            if(map.containsKey(genre)){
                Integer count=map.get(genre);
                map.put(genre,count+1);
            }
            else{
                map.put(genre,1);
            }
        }

        Map<String,Double> score=new HashMap<>();
        Map<String,ArrayList<String>> map1=ratingsDao.findAverageRatingsByGenre();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            ArrayList<String> aList = map1.get(key);

            int i = 0;
            for (String temp : aList) {
                if (score.containsKey(temp)) {
                    double existingValue = score.get(temp);
                    if (existingValue / Math.pow(2, i) < value / Math.pow(2, i)) {
                        score.put(temp, value / Math.pow(2, i));
                    }
                } else {
                    score.put(temp, value / Math.pow(2, i));
                }
                i++;
            }
        }
        List list = new LinkedList(score.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });
        HashMap sortedHashMap = new LinkedHashMap();
        int count=0;
        for(Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            if(count<10)
                sortedHashMap.put(entry.getKey(), entry.getValue());
            count++;

        }

        final JsonNode json = Json.toJson(sortedHashMap.keySet());
        return ok(json);


    }



    @Transactional
    public Result mostPopular() {

        Logger.debug("initial list"+movieList);
        Calendar calendar = Calendar.getInstance();
        Logger.debug("last refesh  "+ lastRefresh);
        Logger.debug("next refresh"+ nextRefresh);

        Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
        Logger.debug("current time " + currentTime);

              if(movieList.isEmpty() || currentTime.after(nextRefresh))
        {

            movieList.clear();
            TypedQuery<Views> query = jpaApi.em().createQuery("select v from Views v ORDER BY views DESC ", Views.class);
            query.setMaxResults(15);
            List<Views> Result = query.getResultList();
            for (Views v : Result) {
                movieList.add(v.getImdbID());


            }

            final JsonNode jsonNode = Json.toJson(movieList);
            lastRefresh.setTime(calendar.getTimeInMillis());
            Logger.debug("time after populating the list" + lastRefresh);

            return ok(jsonNode);

        }
        Logger.debug("in the else case");

        final JsonNode jsonNode = Json.toJson(movieList);
        return ok(jsonNode);


    }




}
