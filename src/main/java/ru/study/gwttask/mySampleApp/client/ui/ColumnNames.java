package ru.study.gwttask.mySampleApp.client.ui;

import ru.study.gwttask.mySampleApp.shared.Book;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum of Column names with filter functionality
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
        public String toString() {
            return LocalUtil.getLocal().name();
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
        public String toString() {
            return LocalUtil.getLocal().author();
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
        public String toString() {
            return LocalUtil.getLocal().isbn();
        }
    },

    EDIT {
        @Override
        public String toString() {
            return LocalUtil.getLocal().edit();
        }
    },

    SELECT {
        @Override
        public String toString() {
            return LocalUtil.getLocal().select();
        }
    },

    DATE {
        @Override
        public String toString() {
            return LocalUtil.getLocal().date();
        }
    };

    public List<Book> filter(List<Book> books, String filterExpression) {
        throw new UnsupportedOperationException("Filtering by " +  this.toString() + " column doesn't support");
    }
}
