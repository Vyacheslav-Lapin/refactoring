package ru.vlapin.examples.dbcp;

public class ConnectionPoolException extends Exception {

    public ConnectionPoolException(String message, Exception e){
        super(message, e);
    }


}
