package ru.study.gwttask.mySampleApp.client;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ListDataProvider;
import ru.study.gwttask.mySampleApp.client.ui.DataTable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class MySampleApp implements EntryPoint {
    private static class Contact {
        private final String address;
        private final Date birthday;
        private final String name;
        private final CheckBox checkBox;

        public Contact(String name, Date birthday, String address, CheckBox checkBox) {
            this.name = name;
            this.birthday = birthday;
            this.address = address;
            this.checkBox = checkBox;
        }
    }

    /**
     * The list of data to display.
     */
    private static final List<Contact> CONTACTS = Arrays.asList(
            new Contact("John", new Date(80, 4, 12), "123 Fourth Avenue", new CheckBox()),
            new Contact("Joe", new Date(85, 2, 22), "22 Lance Ln", new CheckBox()),
            new Contact("George", new Date(46, 6, 6), "1600 Pennsylvania Avenue", new CheckBox()));
    @Override
    public void onModuleLoad() {
        // Create a CellTable.
        CellTable<Contact> table = new CellTable<>();
        table.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);

        // Add a text column to show the name.
        TextColumn<Contact> nameColumn = new TextColumn<Contact>() {
            @Override
            public String getValue(Contact object) {
                return object.name;
            }
        };
        table.addColumn(nameColumn, "Name");

        // Add a date column to show the birthday.
        DateCell dateCell = new DateCell();
        Column<Contact, Date> dateColumn = new Column<Contact, Date>(dateCell) {
            @Override
            public Date getValue(Contact object) {
                return object.birthday;
            }
        };
        table.addColumn(dateColumn, "Birthday");

        // Add a text column to show the address.
        TextColumn<Contact> addressColumn = new TextColumn<Contact>() {
            @Override
            public String getValue(Contact object) {
                return object.address;
            }
        };
        table.addColumn(addressColumn, "Address");

        ButtonCell buttonCell = new ButtonCell();
        Column<Contact, String> buttonColumn = new Column<Contact, String>(buttonCell) {
            @Override
            public String getValue(Contact object) {
                return "Edit";
            }
        };

        buttonColumn.setFieldUpdater((index, object, value) -> Window.alert(object.address));

        table.addColumn(buttonColumn, "Button");

        Column<Contact, Boolean> checkBoxColumn = new Column<Contact, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(Contact object) {
                return false;
            }
        };

        checkBoxColumn.setFieldUpdater(((index, object, value) -> object.checkBox.setValue(!object.checkBox.getValue())));

        table.addColumn(checkBoxColumn);

        // Set the total row count. This isn't strictly necessary, but it affects
        // paging calculations, so its good habit to keep the row count up to date.
        table.setRowCount(CONTACTS.size(), true);

        // Push the data into the widget.
        table.setRowData(0, CONTACTS);

        ListDataProvider<Contact> listDataProvider = new ListDataProvider<>();
        listDataProvider.addDataDisplay(table);

        listDataProvider.getList().addAll(CONTACTS);
        listDataProvider.refresh();

        Button createButton = new Button("Create");
        createButton.addClickHandler(event -> {
            listDataProvider.getList().removeIf(contact -> contact.checkBox.getValue());
            listDataProvider.refresh();
        });

        // Add it to the root panel.
//        RootPanel.get().add(table);
//        RootPanel.get().add(createButton);
        DataTable dataTable = new DataTable();

        RootPanel.get().add(dataTable);

    }
}
