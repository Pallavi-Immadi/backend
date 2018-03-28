package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.Authenticator;
import controllers.security.IsAdmin;
import daos.UserDao;
import models.User;
import play.Logger;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.Results;

import javax.inject.Inject;
import javax.management.relation.Role;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

public class AdminController extends Controller {

    private UserDao userDao;
    private JPAApi jpaApi;

    @Inject
    public AdminController (UserDao userDao,JPAApi jpaApi) {

        this.userDao = userDao;
        this.jpaApi=jpaApi;
    }


    @Transactional
    public Result createUser() throws NoSuchAlgorithmException {

        final JsonNode jsonNode = request().body().asJson();
        final String username = jsonNode.get("username").asText();
        final String password = jsonNode.get("password").asText();
        final String ph = jsonNode.get("phone").asText();
        final String proname = jsonNode.get("proname").asText();

        final long phone=Long.parseLong(ph);


        if (null == username) {
            return badRequest("Missing user name");
        }
        if (null == password) {
            return badRequest("Missing password");
        }

        if (null == ph) {
            return badRequest("Missing contact number");
        }
        if (null == proname) {
            return badRequest("Missing profile name");
        }



        TypedQuery<User> query = jpaApi.em().createQuery("select u from User u where uname='" + username + "'", User.class);

        List<User> Result = query.getResultList();

        if(Result.isEmpty()) {

            String salt = Utils.generateSalt();
            User user = new User();
            user.setUname(username);
            user.setSalt(salt);
            String hashedPassword = Utils.generateHashedPassword(password,salt,10);
            user.setPwd(hashedPassword);
            user.setPhone(phone);
            user.setProname(proname);
            user.setRole(User.Role.User);


            userDao.persist(user);


            return created(user.getUname() + "This is your username");
        }
        else
            return status(409,"username already exists");

    }



    @Transactional
    @Authenticator
    @IsAdmin
    public Result deleteUser(){

        final JsonNode jsonNode = request().body().asJson();
        final String username = jsonNode.get("username").asText();

        if (null == username) {
            return badRequest("Missing user name");
        }

        final User user = userDao.deleteUser(username);

        if(null==user){
            return notFound("user with the following username nt found"+username);
        }

        return noContent();
    }


    @Transactional
    public Result updateUser(String username){
        return TODO;

    }


    @Authenticator
    public Result getCurrentUser() {

        Logger.debug("Get current user");

        final User user = (User) ctx().args.get("user");

        Logger.debug("User: {}", user);

        final JsonNode json = Json.toJson(user);
        return ok(json);
    }




    @Transactional
    @Authenticator
    public Result getAllUsers(){

        final List<User> users = userDao.findAll();

        final JsonNode jsonNode = Json.toJson(users);

        return ok(jsonNode);

    }


}