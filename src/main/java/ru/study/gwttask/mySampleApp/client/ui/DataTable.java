package ru.study.gwttask.mySampleApp.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import ru.study.gwttask.mySampleApp.client.DBService;
import ru.study.gwttask.mySampleApp.client.DBServiceAsync;
import ru.study.gwttask.mySampleApp.shared.Book;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DataTable extends Composite {
    interface DataTableUiBinder extends UiBinder<Widget, DataTable> {
    }

    private DBServiceAsync serviceAsync = GWT.create(DBService.class);

    private static DataTableUiBinder ourUiBinder = GWT.create(DataTableUiBinder.class);

    /*------------------------------------------ Table Part --------------------------------------------*/

    private static int rowNumber = 1;

    @UiField(provided = true)
    FlexTable flexTable = new FlexTable();

    enum Columns {
        NAME,
        AUTHOR,
        ISBN,
        EDIT,
        SELECT
    }

    @UiField(provided = true)
    Button createButton = new Button("Add");

    @UiField(provided = true)
    Button removeButton = new Button("Remove");

    public DataTable() {
        for (Columns currentColumn : Columns.values()) {
            flexTable.setText(0, currentColumn.ordinal(), currentColumn.toString());
        }

        serviceAsync.findAll(new AsyncCallback<List<Book>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(List<Book> result) {
                for (Book currentBook : result) {
                    addRow(currentBook);
                }
            }
        });

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    private void addRow(Book book) {
        flexTable.setText(rowNumber, Columns.NAME.ordinal(), book.getName());
        flexTable.setText(rowNumber, Columns.AUTHOR.ordinal(), book.getAuthor());
        flexTable.setText(rowNumber, Columns.ISBN.ordinal(), String.valueOf(book.getIsbn()));
        flexTable.setWidget(rowNumber, Columns.EDIT.ordinal(), createEditButton(rowNumber));
        flexTable.setWidget(rowNumber, Columns.SELECT.ordinal(), createCheckBox());

        rowNumber++;
    }

    @UiHandler("createButton")
    void onCreateButtonClick(ClickEvent event) {
        createDialog(true, -1);
    }

    @UiHandler("removeButton")
    void onRemoveButtonClick(ClickEvent event) {
        LinkedList<Integer> rows = new LinkedList<>();
        for (int i = 1; i < flexTable.getRowCount(); i++) {
            if (((CheckBox) flexTable.getWidget(i, Columns.SELECT.ordinal())).getValue()) {
                rows.addFirst(i);
            }
        }

        removeRows(rows);
    }

    private void removeRows(List<Integer> rows) {
        List<Long> isbns = new LinkedList<>();
        for (Integer currentRowId : rows) {
            isbns.add(Long.parseLong(flexTable.getText(currentRowId, Columns.ISBN.ordinal())));
        }

        serviceAsync.removeByIsbn(isbns, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Void result) {

            }
        });

        for (Integer currentRowId : rows) {
            flexTable.removeRow(currentRowId);
        }
    }

    // only for tests
//    @Deprecated
//    public void addRandomRow() {
//        Book book = new Book();
//        book.setName(String.valueOf(new Random().nextInt(100)));
//        book.setAuthor(String.valueOf(new Random().nextInt(100)));
//        book.setIsbn(getRandomIsbn());
//
//        serviceAsync.save(book, new AsyncCallback<Book>() {
//            @Override
//            public void onFailure(Throwable caught) {
//
//            }
//
//            @Override
//            public void onSuccess(Book result) {
//
//            }
//        });
//    }

    private long getRandomIsbn() {
        return Math.abs(new Random().nextLong());
    }

    private Button createEditButton(int id) {
        return new Button("Edit", (ClickHandler) event -> createDialog(false, id));
    }

    private CheckBox createCheckBox() {
        return new CheckBox();
    }

    // Modal Dialog
    private DialogBox createDialog(boolean isNewBook, int id) {
        final DialogBox dialog = new DialogBox(false, true);
        Button saveButton = new Button("Save");
        Button closeButton = new Button("Close", (ClickHandler) event -> dialog.hide());
        TextBox nameTextBox = new TextBox();
        TextBox authorTextBox = new TextBox();
        TextBox isbnTextBox = new TextBox();
        Label nameLabel = new Label("Name: ");
        Label authorLabel = new Label("Author: ");
        Label isbnLabel = new Label("ISBN: ");

        saveButton.addClickHandler(event -> {
            Book newBook = new Book();
            newBook.setName(nameTextBox.getText());
            newBook.setAuthor(authorTextBox.getText());
            newBook.setIsbn(Long.parseLong(isbnTextBox.getText()));

            if (isNewBook) {
                serviceAsync.save(newBook, new AsyncCallback<Book>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Book result) {
                        addRow(newBook);
                    }
                });
            } else {
                Book oldBook = new Book();
                oldBook.setName(flexTable.getText(id, 0));
                oldBook.setAuthor(flexTable.getText(id, 1));
                oldBook.setIsbn(Long.parseLong(flexTable.getText(id, 2)));

                serviceAsync.editBook(oldBook, newBook, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Void result) {
                        flexTable.setText(id, Columns.NAME.ordinal(), nameTextBox.getText());
                        flexTable.setText(id, Columns.AUTHOR.ordinal(), authorTextBox.getText());
                        flexTable.setText(id, Columns.ISBN.ordinal(), isbnTextBox.getText());
                    }
                });
            }
        });

        dialog.setAnimationEnabled(true);
        dialog.setGlassEnabled(true);
        dialog.setPopupPosition(500, 150);

        if (!isNewBook) {
            nameTextBox.setText(flexTable.getText(id, Columns.NAME.ordinal()));
            authorTextBox.setText(flexTable.getText(id, Columns.AUTHOR.ordinal()));
            isbnTextBox.setText(flexTable.getText(id, Columns.ISBN.ordinal()));
        }

        HorizontalPanel namePanel = new HorizontalPanel();
        namePanel.add(nameLabel);
        namePanel.add(nameTextBox);

        HorizontalPanel authorPanel = new HorizontalPanel();
        authorPanel.add(authorLabel);
        authorPanel.add(authorTextBox);

        HorizontalPanel isbnPanel = new HorizontalPanel();
        isbnPanel.add(isbnLabel);
        isbnPanel.add(isbnTextBox);

        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.setSpacing(2);
        vPanel.add(namePanel);
        vPanel.add(authorPanel);
        vPanel.add(isbnPanel);
        vPanel.add(buttonPanel);

        dialog.setWidget(vPanel);
        dialog.show();

        return dialog;
    }

    /*------------------------------------------ Filter Part --------------------------------------------*/


}