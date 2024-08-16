package com.coppel.execeptions;

public class AppNotFoundHandler extends Exception {
    public AppNotFoundHandler(String message) {
        super(message);
    }
}
