package ru.study.gwttask.mySampleApp.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.study.gwttask.mySampleApp.shared.Book;

import java.util.List;

@RemoteServiceRelativePath("DBService")
public interface DBService extends RemoteService {
    List<Book> findAll();

    Book findById(int id);

    void removeById(int id);

    Book save(Book book);

    void removeByIsbn(List<Long> isbns);

    void editBook(Book fromBook, Book toBook);
}
