package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.Results;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.awt.*;
import java.util.List;

public class LoginController extends Controller {

    private JPAApi jpaApi;

    @Inject
    public LoginController(JPAApi jpaApi) {
        this.jpaApi=jpaApi;
    }


    @Transactional
   public Result verify() {


        final JsonNode jsonNode = request().body().asJson();
        final String username = jsonNode.get("username").asText();
        final String password = jsonNode.get("password").asText();


        if (null == username) {
            return badRequest("Missing user name");
        }
        if (null == password) {
            return badRequest("Missing password");
        }

        TypedQuery<User> query = jpaApi.em().createQuery("select u from User u where uname='" + username + "'", User.class);

        List<User> Result = query.getResultList();

        for (User p : Result) {

            System.out.print(p.getPwd());// Object[] array=users.toArray();


            if (password.equals(p.getPwd())) {

                final JsonNode jsonNode1 = Json.toJson(Result);

                return ok("Successful");
            }
            else
                return ok("Invalid parameters");
        }
        return (badRequest());
    }
}
