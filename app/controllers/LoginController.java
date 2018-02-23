package controllers;

import akka.http.impl.util.Timestamp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.Authenticator;
import controllers.security.IsAdmin;
import daos.UserDao;
import models.User;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.sql.Update;
import play.Logger;
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
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static play.db.jpa.JPA.em;

public class LoginController extends Controller {

    private JPAApi jpaApi;
    UserDao userDao;


    @Inject
    public LoginController(UserDao userDao, JPAApi jpaApi) {
        this.jpaApi = jpaApi;
        this.userDao = userDao;
    }


    @Transactional
    public Result verify() throws NoSuchAlgorithmException {

        User user = new User();
        final JsonNode jsonNode = request().body().asJson();
        final String username = jsonNode.get("username").asText();
        final String password = jsonNode.get("password").asText();

        if (null == username || null == password) {
            return badRequest();
        }

        JsonNode result = userDao.findByName(username);
        if (null == result) {

            return status(401, "No user specified");

        }


        String salt = result.findValue("salt").asText();
        String hashPwd = Utils.generateHashedPassword(password, salt, 10);
        Logger.debug("Hashed password : " + hashPwd);

        if (hashPwd.equals(result.findValue("pwd").asText())) {

            String token = Utils.generateToken();

            String reftoken = Utils.generateToken();
            Long threshold = Utils.generateThreshold();

            String sql = "update User SET reftoken = '" + reftoken + "',threshold = '" + threshold + "',token = '" + token + "' where uname = '" + username + "';";
            Query query = em().createNativeQuery(sql);
            int updateCount = query.executeUpdate();
            Logger.debug("update count" + updateCount);

            ObjectNode result1 = Json.newObject();
            result1.put("access_token", token);
            result1.put("expiry_time", threshold);
            result1.put("refresh_token", reftoken);

            return ok(result1);
        } else
            return unauthorized("Invalid password");
    }

    @Transactional
    public Result verifyRefreshToken(String reftoken) {


        User refkey = userDao.findByRefreshToken(reftoken);
        Logger.debug("Find by ref token : " + refkey);

        if (null == refkey) {

            return status(401, "No refresh token specified");

        } else {
            Logger.debug("refresh token :" + String.valueOf(refkey));
            String newtoken = Utils.generateToken();
            Long newthreshold = Utils.generateThreshold();
            User user = new User();
            user.setToken(newtoken);
            user.setThreshold(newthreshold);
            userDao.persist(user);

            ObjectNode result2 = Json.newObject();
            result2.put("access_token", newtoken);
            result2.put("expiry_time", newthreshold);

            return ok(result2);
        }

    }


    @Authenticator
    public Result getCurrentUser() {

        Logger.debug("Get current user");

        final User user = (User) ctx().args.get("user");

        Logger.debug("User: {}", user);

        final JsonNode json = Json.toJson(user);
        return ok(json);
}


}
