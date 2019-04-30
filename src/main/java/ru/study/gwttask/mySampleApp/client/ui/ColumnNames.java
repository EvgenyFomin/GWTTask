package ru.study.gwttask.mySampleApp.client.ui;

import ru.study.gwttask.mySampleApp.shared.Book;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum of Column names with filter and sort functionality
*/

public enum ColumnNames {
    NAME {
        @Override
        public List<Book> filter(List<Book> books, String filterExpression) {
            return books
                    .stream()
                    .filter(book -> book.getName().contains(filterExpression))
                    .collect(Collectors.toList());
        }

        @Override
        public List<Book> sort(List<Book> books) {
            books.sort(Comparator.comparing(Book::getName));
            return books;
        }
    },

    AUTHOR {
        @Override
        public List<Book> filter(List<Book> books, String filterExpression) {
            return books
                    .stream()
                    .filter(book -> book.getAuthor().contains(filterExpression))
                    .collect(Collectors.toList());
        }

        @Override
        public List<Book> sort(List<Book> books) {
            books.sort(Comparator.comparing(Book::getAuthor));
            return books;
        }
    },

    ISBN {
        @Override
        public List<Book> filter(List<Book> books, String filterExpression) {
            return books
                    .stream()
                    .filter(book -> String.valueOf(book.getIsbn()).contains(filterExpression))
                    .collect(Collectors.toList());
        }

        @Override
        public List<Book> sort(List<Book> books) {
            books.sort(Comparator.comparingLong(Book::getIsbn));
            return books;
        }

    }, EDIT, SELECT;

    public List<Book> filter(List<Book> books, String filterExpression) {
        throw new UnsupportedOperationException("Filtering by " +  this.toString() + " column doesn't support");
    }

    public List<Book> sort(List<Book> books) {
        throw new UnsupportedOperationException("Sorting by " +  this.toString() + " column doesn't support");
    }
}
