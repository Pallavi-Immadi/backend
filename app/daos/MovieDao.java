package daos;

import models.Movie;
import play.db.jpa.JPAApi;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class MovieDao{

    private JPAApi jpaApi;

    @Inject
    public MovieDao(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    public Movie persist(Movie movie) {

        jpaApi.em().persist(movie);

        return movie;
    }

    public Movie deleteMovie(String imdbID) {

        final Movie movie=findById(imdbID);

        if (null == movie) {
            return null;
        }

        jpaApi.em().remove(movie);

        return movie;
    }

    public Movie findById(String imdbID) {

        final Movie movie = jpaApi.em().find(Movie.class, imdbID);
        return movie;

    }

    public List<String> findGenre(String imdbID){

        Movie movie=findById(imdbID);

        List<String> genreList = new ArrayList<>();
        String genre=movie.getGenre();
        String[] genres=genre.split(", ");
        for(String gen:genres)
        {
            genreList.add(gen);
        }

        return genreList;


    }


    public List<Movie> findAll() {

        TypedQuery<Movie> query = jpaApi.em().createQuery("SELECT m FROM Movie m", Movie.class);
        List<Movie> movies = query.getResultList();

        return movies;
    }

}


