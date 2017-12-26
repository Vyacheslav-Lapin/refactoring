package ru.vlapin.examples.dbcp;

import lombok.SneakyThrows;

import java.sql.*;

public class ConnectToDB {

    @SneakyThrows
    public static void main(String... args) {
        Class.forName("org.h2.Driver");

        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
             Statement st = con.createStatement()) {

            System.out.println("Соединение установлено.");

            st.executeUpdate("CREATE TABLE students (id IDENTITY, name VARCHAR NOT NULL, id_group INT)");

            int countRows = st.executeUpdate(
                    "INSERT INTO students (name, id_group) VALUES ('Баба-Яга', 123456)");

            try (ResultSet rs = st.executeQuery("SELECT * FROM students")) {
                while (rs.next()) {
                    System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3));
                }
            }
        }
    }
}
