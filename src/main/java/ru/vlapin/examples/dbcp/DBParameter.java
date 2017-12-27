package ru.vlapin.examples.dbcp;

import lombok.SneakyThrows;
import lombok.val;

import java.io.FileInputStream;
import java.util.Properties;

public interface DBParameter {
    String DB_DRIVER = "driver";
    String DB_URL = "url";
    String DB_USER = "user";
    String DB_PASSWORD = "password";
    String DB_POLL_SIZE = "poolsize";

    @SneakyThrows
    static Properties getLoad(String fileName) {
        val properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            properties.load(fileInputStream);
        }
        return properties;
    }
}
