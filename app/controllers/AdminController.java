package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import daos.UserDao;
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

public class AdminController extends Controller {

    private UserDao userDao;

    @Inject
    public AdminController (UserDao userDao) {
        this.userDao = userDao;
    }


    @Transactional
    public Result createUser() {

        final JsonNode jsonNode = request().body().asJson();
        final String username = jsonNode.get("username").asText();
        final String password = jsonNode.get("password").asText();
        final String role = jsonNode.get("role").asText();
        final String ph = jsonNode.get("phone").asText();
        final String proname = jsonNode.get("proname").asText();

        final long phone=Long.parseLong(ph);


        if (null == username) {
            return badRequest("Missing user name");
        }
        if (null == password) {
            return badRequest("Missing password");
        }
        if (null == role) {
            return badRequest("Missing role");
        }
       // if (null == phone) {
            //return badRequest("Missing contact number");
       // }
        if (null == proname) {
            return badRequest("Missing profile name");
        }



        User user = new User();
        user.setPwd(password);
        user.setUname(username);
        user.setRole(role);
        user.setPhone(phone);
        user.setProname(proname);

        user=userDao.persist(user);

        return created(user.getUname() + "This is your username");

    }

    @Transactional
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

    @Transactional
    public Result getAllUsers(){

        final List<User> users = userDao.findAll();

        final JsonNode jsonNode = Json.toJson(users);

        return ok(jsonNode);

    }

}