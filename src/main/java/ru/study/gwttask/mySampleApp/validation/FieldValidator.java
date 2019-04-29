package ru.study.gwttask.mySampleApp.validation;

public class FieldValidator {
    public static boolean isValid(String msg) {
        return msg != null && msg.length() >= 5;
    }
}
