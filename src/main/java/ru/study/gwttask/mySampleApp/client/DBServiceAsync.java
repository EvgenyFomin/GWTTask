package ru.study.gwttask.mySampleApp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.study.gwttask.mySampleApp.shared.Book;

import java.util.List;

public interface DBServiceAsync {
    void findAll(AsyncCallback<List<Book>> async);

    void findById(int id, AsyncCallback<Book> async);

    void save(Book book, AsyncCallback<Book> async);

    void removeById(int id, AsyncCallback<Void> async);

    void editBook(Book fromBook, Book toBook, AsyncCallback<Void> async);

    void removeByIsbn(List<Long> isbns, AsyncCallback<Void> async);
}
