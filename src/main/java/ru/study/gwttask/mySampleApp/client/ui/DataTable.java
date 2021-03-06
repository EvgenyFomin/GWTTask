package ru.study.gwttask.mySampleApp.client.ui;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
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
import ru.study.gwttask.mySampleApp.client.ui.localization.ResourceBundle;
import ru.study.gwttask.mySampleApp.shared.Book;

import java.util.*;
import java.util.stream.Collectors;

public class DataTable extends Composite {
    interface DataTableUiBinder extends UiBinder<Widget, DataTable> {
    }

    private DBServiceAsync serviceAsync = GWT.create(DBService.class);

    private static DataTableUiBinder ourUiBinder = GWT.create(DataTableUiBinder.class);

    private static ResourceBundle local = LocalUtil.getLocal();

    @UiField(provided = true)
    HorizontalPanel fullPanel = new HorizontalPanel() {{
        setStyleName("container row");
    }};

    private ListDataProvider<Book> listDataProvider = new ListDataProvider<>();

    private VerticalPanel tablePanel = new VerticalPanel();

    private CellTable<Book> cellTable = new CellTable<>();

    private Button createButton = new Button(local.add(), getAddClickHandler());

    private Button removeButton = new Button(local.remove(), getRemoveClickHandler());

    private HashSet<Long> isbns = new HashSet<>();

    private boolean filterMode = false;

    private TextBox filterTextBox = new TextBox();

    private List<Book> filterCache = new LinkedList<>();

    private VerticalPanel filterPanel = new VerticalPanel();

    private ColumnNames ownerColumnNames; // Column which Radio Button has enabled

    public DataTable() {
        createFilterPart();
        createTablePart();
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    private void createFilterPart() {
        VerticalPanel radioPanel = new VerticalPanel();

        final RadioButton nameRButton = new RadioButton("Filter", ColumnNames.NAME.toString()) {{
            setValue(true);
        }};

        ownerColumnNames = ColumnNames.NAME;

        final RadioButton authorRButton = new RadioButton("Filter", ColumnNames.AUTHOR.toString());
        final RadioButton isbnRButton = new RadioButton("Filter", ColumnNames.ISBN.toString());

        nameRButton.addClickHandler(event -> ownerColumnNames = ColumnNames.NAME);
        authorRButton.addClickHandler(event -> ownerColumnNames = ColumnNames.AUTHOR);
        isbnRButton.addClickHandler(event -> ownerColumnNames = ColumnNames.ISBN);

        radioPanel.add(nameRButton);
        radioPanel.add(authorRButton);
        radioPanel.add(isbnRButton);

        radioPanel.add(new Label(local.expression()));
        radioPanel.add(filterTextBox);

        Button findButton = new Button(local.find());
        findButton.addClickHandler(getFindClickHandler());

        Button resetButton = new Button(local.reset());
        resetButton.addClickHandler(getResetClickHandler());

        radioPanel.add(new HorizontalPanel() {{
            add(findButton);
            add(resetButton);
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
        cellTable.addColumn(nameColumn, ColumnNames.NAME.toString());

        TextColumn<Book> authorColumn = new TextColumn<Book>() {
            @Override
            public String getValue(Book object) {
                return object.getAuthor();
            }
        };
        cellTable.addColumn(authorColumn, ColumnNames.AUTHOR.toString());

        TextColumn<Book> isbnColumn = new TextColumn<Book>() {
            @Override
            public String getValue(Book object) {
                return String.valueOf(object.getIsbn());
            }
        };
        cellTable.addColumn(isbnColumn, ColumnNames.ISBN.toString());

        Column<Book, String> editColumn = new Column<Book, String>(new ButtonCell()) {
            @Override
            public String getValue(Book object) {
                return local.edit();
            }
        };
        editColumn.setFieldUpdater(((index, object, value) -> createDialog(object)));
        cellTable.addColumn(editColumn, ColumnNames.EDIT.toString());

        Column<Book, Boolean> checkBoxColumn = new Column<Book, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(Book object) {
                return false;
            }
        };
        checkBoxColumn.setFieldUpdater((index, object, value) -> object.setChecked(value));
        cellTable.addColumn(checkBoxColumn, ColumnNames.SELECT.toString());

        TextColumn<Book> dateColumn = new TextColumn<Book>() {
            @Override
            public String getValue(Book object) {
                return String.valueOf(object.getDate());
            }
        };
        cellTable.addColumn(dateColumn, ColumnNames.DATE.toString());

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

                isbns.addAll(listDataProvider.getList().stream().map(Book::getIsbn).collect(Collectors.toList()));
            }
        });

        tablePanel.add(cellTable);
        tablePanel.add(new HorizontalPanel() {{
            add(createButton);
            add(removeButton);
        }});

        fullPanel.add(tablePanel);
    }

    // Rows manipulation
    private void addRows(List<Book> books) {
        if (filterMode) {
            filterCache.addAll(books);
            listDataProvider.getList().addAll(ownerColumnNames.filter(books, filterTextBox.getText()));
        } else {
            listDataProvider.getList().addAll(books);
            listDataProvider.refresh();
        }
    }

    private void removeRows(List<Book> booksToRemove) {
        listDataProvider.getList().removeAll(booksToRemove);
        listDataProvider.refresh();

        if (filterMode) {
            filterCache.removeAll(booksToRemove);
        }
    }

    // ClickHandlers
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
                    removeRows(booksToRemove);
                }
            });
        };
    }

    private ClickHandler getFindClickHandler() {
        return event -> {
            if (!filterTextBox.getText().isEmpty()) {

                // Activating filter mode and copying data to cache
                if (!filterMode) {
                    filterMode = true;
                    filterCache.addAll(listDataProvider.getList());
                }

                List<Book> newList = ownerColumnNames.filter(filterCache, filterTextBox.getText());
                listDataProvider.setList(newList);

                listDataProvider.refresh();
            } else {
                if (filterMode) {
                    filterMode = false;
                    listDataProvider.getList().clear();
                    listDataProvider.getList().addAll(filterCache);
                    listDataProvider.refresh();

                    filterCache.clear();
                }
            }
        };
    }

    private ClickHandler getResetClickHandler() {
        return event -> {
            if (filterMode) {
                filterMode = false;
                filterTextBox.setText("");
                listDataProvider.getList().clear();
                listDataProvider.getList().addAll(filterCache);
                listDataProvider.refresh();

                filterCache.clear();
            }
        };
    }

    // Modal Dialog
    private DialogBox createDialog(Book book) {
        boolean isNewBook = (book == null);
        final DialogBox dialog = new DialogBox(true, true);

        Button saveButton = new Button(isNewBook ? local.add() : local.save());
        Button closeButton = new Button(local.cancel(), event -> dialog.hide());
        TextBox nameTextBox = new TextBox();
        TextBox authorTextBox = new TextBox();
        TextBox isbnTextBox = new TextBox();
        Label nameLabel = new Label(local.name() + ":");
        Label authorLabel = new Label(local.author() + ":");
        Label isbnLabel = new Label(local.isbn() + ":");
        Label errorLabel = new Label() {{
            setStyleName("text-error");
        }};

        saveButton.addClickHandler(event -> {
            Book newBook = new Book();
            newBook.setName(nameTextBox.getText());
            newBook.setAuthor(authorTextBox.getText());
            newBook.setDate(new Date().toString());
            String errorMsg = "";

            try {
                newBook.setIsbn(Long.parseLong(isbnTextBox.getText()));
            } catch (NumberFormatException e) {
                errorMsg = local.incorrectIsbn();
                errorLabel.setText(errorMsg);
            }

            if (errorMsg.isEmpty()) {
                errorMsg = isValidBook(newBook);
            }

            errorLabel.setText(!errorMsg.isEmpty() ? errorMsg : "");

            if (errorMsg.isEmpty()) {
                if (isNewBook) {
                    serviceAsync.save(newBook, new AsyncCallback<Book>() {
                        @Override
                        public void onFailure(Throwable caught) {

                        }

                        @Override
                        public void onSuccess(Book result) {
                            addRows(Collections.singletonList(newBook));
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

                            if (filterMode) {
                                Collections.replaceAll(filterCache, book, newBook);
                            }
                        }
                    });
                }
            }
        });

        dialog.setAnimationEnabled(true);
        dialog.setGlassEnabled(true);

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

        VerticalPanel editPanel = new VerticalPanel();
        editPanel.add(namePanel);
        editPanel.add(authorPanel);
        editPanel.add(isbnPanel);
        editPanel.add(errorLabel);
        editPanel.add(buttonPanel);

        dialog.setWidget(editPanel);
        dialog.show();

        return dialog;
    }

    // Validation
    private String isValidBook(Book book) {
        if (book.getName().isEmpty()) {
            return local.booksNameEmpty();
        } else if (book.getAuthor().isEmpty()) {
            return local.booksAuthorEmpty();
        } else if (book.getIsbn() < 0) {
            return local.incorrectIsbn();
        } else if (listDataProvider.getList().contains(book) || filterCache.contains(book)) {
            return local.bookAlreadyExists();
        } else if (!isbns.add(book.getIsbn())) {
            return local.isbnAlreadyExists();
        } else {
            return "";
        }
    }
}