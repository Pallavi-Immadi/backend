package models;


import scala.Int;

import javax.persistence.*;

@Entity
public class Views {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    public Views(Integer id, Movie imdbID, Integer views) {
        this.id = id;
        this.imdbID = imdbID;
        this.views = views;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToOne
    Movie imdbID;

    @Basic
    Integer views;


    public Views(){}

    public Movie getImdbID() {
        return imdbID;
    }

    public void setImdbID(Movie imdbID) {
        this.imdbID = imdbID;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }
}
