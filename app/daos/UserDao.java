package daos;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import models.User;
import play.Logger;
import play.db.jpa.JPAApi;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class UserDao {

    private JPAApi jpaApi;

    @Inject
    public UserDao(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    public User persist(User user) {


        jpaApi.em().persist(user);

        return user;
    }

    public User deleteUser(String username) {

        final User user = findUserByName(username);
        if (null == user) {
            return null;
        }

        jpaApi.em().remove(user);
        Query query=jpaApi.em().createQuery("delete from Ratings where userId="+user.getId());

        return user;
    }



   /* public User findById(String username) {

        final User user = jpaApi.em().find(User.class, username);//Id cannot be a string hence using findUserbyName()
        return user;

    */


    public JsonNode findByName(String username) {

        TypedQuery<User> query = jpaApi.em().createQuery("select u from User u where uname='" + username + "'", User.class);
        final List<User> Result = query.getResultList();

        if (Result.size() == 0) {
            return null;
        }

        final JsonNode json = Json.toJson(Result);
        return json;
    }

    public User findByToken(String token) {

        TypedQuery<User> query = jpaApi.em().createQuery("SELECT u FROM User u where token = :token", User.class);
        // Logger.debug("Query result : " + query);
        query.setParameter("token",token);
        List<User> result1 = query.getResultList();
        if(result1.isEmpty())
        {
            return null;

        }

        return result1.get(0);


    }

    public User findByRefreshToken(String reftoken) {

        TypedQuery<User> query = jpaApi.em().createQuery("SELECT u FROM User u where reftoken = :reftoken", User.class);
        //  Logger.debug("Query result : " + query);
        query.setParameter("reftoken",reftoken);
        List<User> result1 = query.getResultList();
        if (result1.size() == 0) {
            return null;
        }


        return result1.get(0);

    }


    public User findUserByName(String username) {

        TypedQuery<User> query = jpaApi.em().createQuery("select u from User u where uname='" + username + "'", User.class);
        final List<User> Result = query.getResultList();

        if (Result.size() == 0) {
            return null;
        }

        return Result.get(0);

    }






    public List<User> findAll() {



        TypedQuery<User> query = jpaApi.em().createQuery("SELECT u FROM User u", User.class);
        List<User> users = query.getResultList();

        return users;
    }


}
