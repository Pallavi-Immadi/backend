package models;

import javax.persistence.*;


@Entity
public class Ratings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Basic
    Integer userId;

    @Basic
    Integer rating;

    @Basic
    String movieId;


    public Ratings(){}

    public Ratings(Integer id, Integer userId, Integer rating, String movieId) {
        this.id = id;
        this.userId = userId;
        this.rating = rating;
        this.movieId = movieId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
