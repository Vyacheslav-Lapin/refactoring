package ru.vlapin.examples.dbcp;

import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.util.Properties;

public class DBResourceManager {
    private final static DBResourceManager instance = new DBResourceManager();

    private Properties bundle = new Properties() {
        @SneakyThrows
        Properties load(String address) {
            try (FileInputStream fileInputStream = new FileInputStream(address)) {
                load(fileInputStream);
            }
            return this;
        }
    }.load("./src/test/resources/db.properties");

    public static DBResourceManager getInstance() {

        return instance;
    }

    public String getValue(String key){
        return bundle.getProperty(key);
    }

}
