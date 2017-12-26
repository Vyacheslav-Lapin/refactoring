package ru.vlapin.examples.dbcp;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Log4j2
public class ConnectToDB {

    @SneakyThrows
    public static void main(String... args) {
        Class.forName("org.h2.Driver");

        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
             Statement st = con.createStatement()) {

            logInfo("Соединение установлено.");

            st.executeUpdate("CREATE TABLE students (id IDENTITY, name VARCHAR NOT NULL, id_group INT)");

            int countRows = st.executeUpdate(
                    "INSERT INTO students (name, id_group) VALUES ('Баба-Яга', 123456)");

            try (ResultSet rs = st.executeQuery("SELECT id, name, id_group FROM students")) {
                while (rs.next())
                    System.out.printf("%d %s %d%n",
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("id_group"));
            }
        }
    }

    private static void logInfo(String message) {
        System.out.println(message);
        log.info(message);
    }
}
