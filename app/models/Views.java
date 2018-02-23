package models;


import javax.persistence.*;

@Entity
public class Views {

    @Id
    String id;

    @OneToOne
    Movie imdbID;

    @Basic
    Integer views;


    public Views(){}

    public Views(String id, Movie imdbID, Integer views) {
        this.id = id;
        this.imdbID = imdbID;
        this.views = views;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
