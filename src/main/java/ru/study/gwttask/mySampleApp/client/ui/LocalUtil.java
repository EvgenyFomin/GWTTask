package ru.study.gwttask.mySampleApp.client.ui;

import com.google.gwt.core.client.GWT;
import ru.study.gwttask.mySampleApp.client.ui.localization.ResourceBundle;

class LocalUtil {
    private static final ResourceBundle local = GWT.create(ResourceBundle.class);

    static ResourceBundle getLocal() {
        return local;
    }
}
