package models;

import javax.persistence.*;

@Entity
public class Movie{

    @Id
    String imdbID;

    @Basic
    String title;

    @Basic
    String certification;

    @Basic
    String runtime;

    @Basic
    Integer year;

    @Basic
    String genre;

    @Basic
    String director;

    @Basic
    String actors;

    @Basic
    String plot;

    @Basic
    String language;

    @Basic
    String poster;



    public Movie()
    {}

    public Movie(String imdbID, String title, String certification, String runtime, Integer year, String genre, String director, String actors, String plot, String language, String poster) {
        this.imdbID = imdbID;
        this.title = title;
        this.certification = certification;
        this.runtime = runtime;
        this.year = year;
        this.genre = genre;
        this.director = director;
        this.actors = actors;
        this.plot = plot;
        this.language = language;
        this.poster = poster;
    }


    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }
}
