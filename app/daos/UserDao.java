package daos;

import models.User;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
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

        final User user = findById(username);
        if (null == user) {
            return null;
        }

        jpaApi.em().remove(user);

        return user;
    }

    public User findById(String username) {

        final User user = jpaApi.em().find(User.class, username);
        return user;

    }

    /*public Book update(Book book) {

        final Book existingBook = findById(book.getId());
        if (null == existingBook) {
            return null;
        }

        existingBook.setTile(book.getTile());
        persist(existingBook);

        return existingBook;
    }*/

    public List<User> findAll() {

        TypedQuery<User> query = jpaApi.em().createQuery("SELECT u FROM User u", User.class);
        List<User> users = query.getResultList();

        return users;
    }

}
