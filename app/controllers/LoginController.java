package controllers;

import java.sql.Timestamp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.mail.smtp.SMTPMessage;
import controllers.security.Authenticator;
import controllers.security.IsAdmin;
import daos.RatingsDao;
import daos.UserDao;
import models.TemporaryStorage;
import models.User;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.sql.Update;
import play.Logger;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.F;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.Results;

import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static play.db.jpa.JPA.em;

public class LoginController extends Controller {

    private JPAApi jpaApi;
    UserDao userDao;
    RatingsDao ratingsDao;
    TemporaryStorage map = new TemporaryStorage();


    @Inject
    public LoginController(UserDao userDao, JPAApi jpaApi) {
        this.jpaApi = jpaApi;
        this.userDao = userDao;
    }


    @Transactional
    public Result verify() throws NoSuchAlgorithmException {


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

        User user = userDao.findUserByName(username);
        //Logger.debug(" th user role is"+  user.getRole());
        String salt = result.findValue("salt").asText();
        String hashPwd = Utils.generateHashedPassword(password, salt, 10);
        // Logger.debug("Hashed password : " + hashPwd);

        if (hashPwd.equals(result.findValue("pwd").asText())) {

            String token = Utils.generateToken();
            User.Role userRole = user.getRole();


            String reftoken = Utils.generateToken();
            Long threshold = Utils.generateThreshold();
            Boolean check = ratingsDao.findUserId(user.getId());

            String sql = "update User SET reftoken = '" + reftoken + "',threshold = '" + threshold + "',token = '" + token + "' where uname = '" + username + "';";
            Query query = em().createNativeQuery(sql);
            int updateCount = query.executeUpdate();


            ObjectNode result1 = Json.newObject();
            result1.put("access_token", token);
            result1.put("expiry_time", threshold);
            result1.put("refresh_token", reftoken);
            result1.put("role",user.getRole().toString());
            result1.put("firstlogin",check);

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



    @Transactional
    @Authenticator
    public Result changePassword() throws NoSuchAlgorithmException {

        final JsonNode jsonNode = request().body().asJson();

        final String old_password = jsonNode.get("old_password").asText();
        final String new_password=jsonNode.get("new_password").asText();


        if(null == old_password){
            return badRequest("Missing password");
        }

        if(null == new_password){
            return badRequest("Missing password");
        }

        final User user = (User) ctx().args.get("user");
        Logger.debug(""+user.getRole());
        String password=user.getPwd();
        String old_salt=user.getSalt();
        String hashedPassword = Utils.generateHashedPassword(old_password,old_salt,10);
        if(password.equals(hashedPassword)) {

            String hashedPassword1 = Utils.generateHashedPassword(new_password, old_salt, 10);
            user.setPwd(hashedPassword1);
            userDao.persist(user);
        }
        else{
            return badRequest("wrong password");
        }

        return ok("changed password");

    }





    @Transactional
    public Result forgotPassword(String uname) {

        User user=userDao.findUserByName(uname);
        Logger.debug("Username/Email is : "+user.getUname());
        if(user == null){

            return badRequest("EmailID or the USerId does not exist");
        }

        String randomToken = Utils.generateToken();
        Long timeStamp = Utils.generateThreshold();

        Logger.debug("Random Token: "+randomToken);
        Logger.debug("timeStamp : "+ timeStamp);

        F.Tuple<User, Long> tuple = new F.Tuple(user, timeStamp);


      // ConcurrentHashMap<String,Tuple> map = new ConcurrentHashMap<>();
      // map.
         map.addMap(randomToken, tuple);


        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");


        String sender = "movieshed123@gmail.com";

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("movieshed123@gmail.com", "stbpstbp");
            }
        });
       // session = Session.getInstance(props, new javax.mail.Authenticator());

        try {

            String url = "http://localhost:3000/forgotpassword/";

            SMTPMessage message = new SMTPMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO,

                    InternetAddress.parse(user.getUname()));

            message.setSubject("Forgot Password");
            message.setText("To change password, click here:\n" + url + randomToken);
            message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return ok();
    }



  @Transactional
    public Result resetPassword() throws NoSuchAlgorithmException{

        final JsonNode jsonNode = request().body().asJson();
        final String newPassword = jsonNode.get("newPassword").asText();
        final String userToken = jsonNode.get("id").asText();

        ConcurrentHashMap<String, F.Tuple<User, Long>> result = map.getMap();

        F.Tuple tuple = result.get(userToken);
        Logger.debug("tuple value" + tuple);

        if (null == tuple) {
            return forbidden();
        }

        User user = (User) tuple._1;
        Long timestamp = (Long) tuple._2;

        Long currentTime = new Timestamp(System.currentTimeMillis()).getTime();

        Logger.debug("Current time: "+currentTime);
        Logger.debug("timestamp: "+timestamp );

        if(currentTime > timestamp) {
            return badRequest("the link is no longer valid,please try again!!!!!");
        }

        String username=user.getUname();
        Logger.debug("Username in tuple: "+username);
        String salt = Utils.generateSalt();

        String hashedPassword = Utils.generateHashedPassword(newPassword, salt, 10);
        User user1=userDao.findUserByName(username);
        user1.setPwd(hashedPassword);
        user1.setSalt(salt);
        userDao.persist(user1);
        Logger.debug("new password: " +hashedPassword );

        return  ok("Successfully reset the password!!!!!");
    }



}
