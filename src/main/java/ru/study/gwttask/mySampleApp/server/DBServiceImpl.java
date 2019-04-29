package ru.study.gwttask.mySampleApp.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.study.gwttask.mySampleApp.client.DBService;
import ru.study.gwttask.mySampleApp.shared.Book;

import java.util.List;

public class DBServiceImpl extends RemoteServiceServlet implements DBService {
    private Session getOpenedSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    @Override
    public List<Book> findAll() {
        try (Session session = getOpenedSession()) {
            return session.createQuery("from Book", Book.class).list();
        }
    }

    @Override
    public Book findById(int id) {
        try (Session session = getOpenedSession()) {
            return session.find(Book.class, id);
        }
    }

    @Override
    public void removeByIsbn(List<Long> isbns) {
        try (Session session = getOpenedSession()) {
            session.beginTransaction();
            for (Long currentIsbn : isbns) {
                Query query = session.createQuery("delete from Book where isbn = :isbn").setParameter("isbn", currentIsbn);
                query.executeUpdate();
            }
            session.getTransaction().commit();
        }
    }

    @Override
    public void editBook(Book fromBook, Book toBook) {
        try (Session session = getOpenedSession()) {
            session.beginTransaction();
            Book book = session
                    .createQuery("from Book where isbn = :isbn", Book.class)
                    .setParameter("isbn", fromBook.getIsbn()).uniqueResult();

            Query query = session.createQuery("update Book set name = :name, author = :author, isbn = :isbn where id = :id");
            query.setParameter("name", toBook.getName());
            query.setParameter("author", toBook.getAuthor());
            query.setParameter("isbn", toBook.getIsbn());
            query.setParameter("id", book.getId());

            query.executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Override
    @Deprecated
    public void removeById(int id) {
        try (Session session = getOpenedSession()) {
            session.beginTransaction();
            Query query = session.createQuery("delete from Book where id = :id").setParameter("id", id);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public Book save(Book book) {
        Session session = getOpenedSession();
        session.beginTransaction();
        session.persist(book);
        session.getTransaction().commit();
        session.close();
        return book;
    }
}