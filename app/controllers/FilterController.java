package controllers;

import ch.qos.logback.classic.spi.LoggerRemoteView;
import com.fasterxml.jackson.databind.JsonNode;
import models.Movie;
import org.hibernate.jpa.criteria.predicate.CompoundPredicate;
import play.Logger;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.lang.reflect.Array;
import java.util.*;

public class FilterController extends Controller {
    private JPAApi jpaApi;

    @Inject
    public FilterController(JPAApi jpaApi) {
        this.jpaApi=jpaApi;
    }


    @Transactional
    public Result findByTitle() {


        final JsonNode jsonNode = request().body().asJson();
        final String title = jsonNode.get("Title").asText();



        if (null == title) {
            return badRequest("Missing title");
        }


        TypedQuery<Movie> query = jpaApi.em().createQuery("select m from Movie m where title='" + title + "'", Movie.class);

        List<Movie> mov = query.getResultList();

        final JsonNode jsonNode1 = Json.toJson(mov);

        return ok(jsonNode1);

    }


    @Transactional
    public Result findByLanguage() {



        final JsonNode jsonNode = request().body().asJson();
         String language = jsonNode.get("language").asText();



        if (null ==language ) {
            return badRequest("missing language");
        }


        TypedQuery<Movie> query = jpaApi.em().createQuery("select m from Movie m where language LIKE      '" +"%"+ language + "%"+"'      ", Movie.class);

        List<Movie> mov = query.getResultList();

        final JsonNode jsonNode1 = Json.toJson(mov);

        return ok(jsonNode1);

    }


    @Transactional
    public Result findByGenre() {


        final JsonNode jsonNode = request().body().asJson();
        String genre = jsonNode.get("genre").asText();



        if (null ==genre ) {
            return badRequest("missing genre");
        }


        TypedQuery<Movie> query = jpaApi.em().createQuery("select m from Movie m where genre LIKE      '" +"%"+ genre + "%"+"'      ", Movie.class);

        List<Movie> mov = query.getResultList();

        final JsonNode jsonNode1 = Json.toJson(mov);

        return ok(jsonNode1);

    }




    @Transactional
   public Result dynamicQuery(){

        final JsonNode jsonNode = request().body().asJson();
        final Iterator<String> itr = jsonNode.fieldNames();
        final int i= jsonNode.size();

        Collection<String> names = new ArrayList<>();
        while (itr.hasNext()) {
            String name = itr.next();
            names.add(name);
        }


        CriteriaBuilder builder = jpaApi.em().getCriteriaBuilder();

        CriteriaQuery criteriaQuery = builder.createQuery();

        Root<Movie> movie = criteriaQuery.from(Movie.class);

        List<Predicate> genres = new ArrayList<Predicate>();

        List<Predicate> languages = new ArrayList<Predicate>();

        for (String name : names) {

            if(jsonNode.get(name).asText().contains(","))
            {
                if(name=="genre") {
                    String[] newNames = jsonNode.get(name).asText().split(",");

                    for (String newName : newNames) {
                       Logger.debug(newName);
                      genres.add(builder.like(movie.get(name), '%' + newName + '%'));
                    }
                }
                else{
                    String[] newNames = jsonNode.get(name).asText().split(",");
                    for (String newName : newNames) {
                        Logger.debug(newName);
                       languages.add(builder.like(movie.get(name), '%' + newName + '%'));
                    }
                }


            }
            else
            {
                if(name=="genre"){
                    genres.add(builder.like(movie.get(name),'%'+jsonNode.get(name).asText()+'%'));
                Logger.debug(jsonNode.get(name).asText());}

                else{
                  languages.add(builder.like(movie.get(name),'%'+jsonNode.get(name).asText()+'%'));
                Logger.debug(jsonNode.get(name).asText());}


            }
        }


        Predicate[] genre_collection =genres.toArray(new Predicate[genres.size()]);

        Predicate com_genre=builder.or(genre_collection);

        Predicate[] language_collection =languages.toArray(new Predicate[languages.size()]);

        Predicate com_language=builder.or(language_collection);

        Predicate pFinal;



        if(genres.isEmpty()) {
            pFinal = builder.or(language_collection);
        }
        else if(languages.isEmpty())
             pFinal=builder.or(genre_collection);
        else
             pFinal=builder.and(com_language,com_genre);

        Logger.debug(pFinal+"");
        criteriaQuery.select(movie).where(pFinal);



        TypedQuery<Movie> query = jpaApi.em().createQuery(criteriaQuery);

        System.out.println(query.getResultList());

        List<Movie> results = query.getResultList();
        final JsonNode jsonNode1 = Json.toJson(results);


        System.out.println(results);
        return ok(jsonNode1);


    }

}


