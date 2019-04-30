package ru.study.gwttask.mySampleApp.client.ui;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import ru.study.gwttask.mySampleApp.client.DBService;
import ru.study.gwttask.mySampleApp.client.DBServiceAsync;
import ru.study.gwttask.mySampleApp.shared.Book;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DataTable extends Composite {
    interface DataTableUiBinder extends UiBinder<Widget, DataTable> {
    }

    private DBServiceAsync serviceAsync = GWT.create(DBService.class);

    private static DataTableUiBinder ourUiBinder = GWT.create(DataTableUiBinder.class);

    /*------------------------------------------ Table Part --------------------------------------------*/

    private ListDataProvider<Book> listDataProvider = new ListDataProvider<>();

    @UiField(provided = true)
    HorizontalPanel fullPanel = new HorizontalPanel();

    VerticalPanel filterPanel = new VerticalPanel();

    VerticalPanel tablePanel = new VerticalPanel();

    CellTable<Book> cellTable = new CellTable<>();

    Button createButton = new Button("Add", getAddClickHandler());

    Button removeButton = new Button("Remove", getRemoveClickHandler());

    private RadioButton ownerButton;

    public DataTable() {
        createFilterPart();
        createTablePart();
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    private void createFilterPart() {
        VerticalPanel radioPanel = new VerticalPanel();

        RadioButton nameRButton = new RadioButton("Name");
        RadioButton authorRButton = new RadioButton("Author");
        RadioButton isbnRButton = new RadioButton("ISBN");

        TextBox valueBox = new TextBox();

        radioPanel.add(new HorizontalPanel() {{
            add(new Label("Name: "));
            add(nameRButton);
        }});

        radioPanel.add(new HorizontalPanel() {{
            add(new Label("Author: "));
            add(authorRButton);
        }});

        radioPanel.add(new HorizontalPanel() {{
            add(new Label("ISBN: "));
            add(isbnRButton);
        }});

        radioPanel.add(new HorizontalPanel() {{
            add(new Label("Filter: "));
            add(valueBox);
        }});

        filterPanel.add(radioPanel);

        fullPanel.add(filterPanel);
    }

    private void createTablePart() {
        TextColumn<Book> nameColumn = new TextColumn<Book>() {
            @Override
            public String getValue(Book object) {
                return object.getName();
            }
        };
        cellTable.addColumn(nameColumn, "Name");

        TextColumn<Book> authorColumn = new TextColumn<Book>() {
            @Override
            public String getValue(Book object) {
                return object.getAuthor();
            }
        };
        cellTable.addColumn(authorColumn, "Author");

        TextColumn<Book> isbnColumn = new TextColumn<Book>() {
            @Override
            public String getValue(Book object) {
                return String.valueOf(object.getIsbn());
            }
        };
        cellTable.addColumn(isbnColumn, "ISBN");

        Column<Book, String> editColumn = new Column<Book, String>(new ButtonCell()) {
            @Override
            public String getValue(Book object) {
                return "Edit";
            }
        };

        editColumn.setFieldUpdater(((index, object, value) -> createDialog(object)));

        cellTable.addColumn(editColumn, "Edit");

        Column<Book, Boolean> checkBoxColumn = new Column<Book, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(Book object) {
                return false;
            }
        };

        checkBoxColumn.setFieldUpdater((index, object, value) -> object.setChecked(value));

        cellTable.addColumn(checkBoxColumn, "Select");

        listDataProvider.addDataDisplay(cellTable);

        serviceAsync.findAll(new AsyncCallback<List<Book>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(List<Book> result) {
                cellTable.setRowCount(result.size(), true);
                cellTable.setRowData(0, result);

                listDataProvider.getList().addAll(result);
                listDataProvider.refresh();
            }
        });

        tablePanel.add(cellTable);
        tablePanel.add(new HorizontalPanel() {{
            add(createButton);
            add(removeButton);
        }});

        fullPanel.add(tablePanel);
    }

    private void addRow(List<Book> books) {
        listDataProvider.getList().addAll(books);
        listDataProvider.refresh();

        //TODO Save to DB
    }

    private ClickHandler getAddClickHandler() {
        return event -> createDialog(null);
    }

    private ClickHandler getRemoveClickHandler() {
        return event -> {
            List<Book> booksToRemove = listDataProvider.getList().stream().filter(Book::isChecked).collect(Collectors.toList());

            serviceAsync.removeByIsbn(booksToRemove.stream().map(Book::getIsbn).collect(Collectors.toList()), new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {

                }

                @Override
                public void onSuccess(Void result) {
                    listDataProvider.getList().removeAll(booksToRemove);
                    listDataProvider.refresh();
                }
            });
        };
    }

//    @UiHandler("createButton")
//    void onCreateButtonClick(ClickEvent event) {
//        createDialog(null);
//    }
//
//    @UiHandler("removeButton")
//    void onRemoveButtonClick(ClickEvent event) {
//        List<Book> booksToRemove = listDataProvider.getList().stream().filter(Book::isChecked).collect(Collectors.toList());
//
//        serviceAsync.removeByIsbn(booksToRemove.stream().map(Book::getIsbn).collect(Collectors.toList()), new AsyncCallback<Void>() {
//            @Override
//            public void onFailure(Throwable caught) {
//
//            }
//
//            @Override
//            public void onSuccess(Void result) {
//                listDataProvider.getList().removeAll(booksToRemove);
//                listDataProvider.refresh();
//            }
//        });
//    }

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

    // Modal Dialog
    private DialogBox createDialog(Book book) {
        boolean isNewBook = (book == null);
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
                        addRow(Collections.singletonList(newBook));
                    }
                });
            } else {
                serviceAsync.editBook(book, newBook, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Void result) {
                        Collections.replaceAll(listDataProvider.getList(), book, newBook);
                        listDataProvider.refresh();
                    }
                });
            }
        });

        dialog.setAnimationEnabled(true);
        dialog.setGlassEnabled(true);
        dialog.setPopupPosition(500, 150);

        if (!isNewBook) {
            nameTextBox.setText(book.getName());
            authorTextBox.setText(book.getAuthor());
            isbnTextBox.setText(String.valueOf(book.getIsbn()));
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