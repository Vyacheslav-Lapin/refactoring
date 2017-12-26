package ru.vlapin.examples.dbcp;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;

@Log4j2
public class ConnectToDB {

    @SneakyThrows
    public static void main(String... args) {
        Class.forName("org.h2.Driver");

        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
             Statement st = con.createStatement()) {

            logInfo("Соединение установлено.");

            init(st);

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

    private static void init(Statement st) throws IOException, SQLException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        ConnectToDB.class.getResourceAsStream("init.sql"),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null)
                st.executeUpdate(line);
        }
    }

    private static void logInfo(String message) {
        System.out.println(message);
        log.info(message);
    }
}
