package ru.study.gwttask.mySampleApp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import ru.study.gwttask.mySampleApp.client.ui.DataTable;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class MySampleApp implements EntryPoint {
    @Override
    public void onModuleLoad() {
        DataTable dataTable = new DataTable();

        RootPanel.get().add(dataTable);

//        RootPanel.get().add(mainUiBinder);
//        RootPanel.get().add(verticalPanel);
    }
}
