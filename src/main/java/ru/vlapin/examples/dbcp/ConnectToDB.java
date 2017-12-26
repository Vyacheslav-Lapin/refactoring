package ru.vlapin.examples.dbcp;

import lombok.SneakyThrows;

import java.sql.*;

public class ConnectToDB {

    @SneakyThrows
    public static void main(String... args) {
        Class.forName("org.h2.Driver");
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = DriverManager
                    .getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

            System.out.println("Соединение установлено.");

            st = con.createStatement();

            st.executeUpdate("CREATE TABLE STUDENTS (id IDENTITY, NAME VARCHAR NOT NULL, id_group INT)");

            int countRows = st.executeUpdate(
                    "INSERT INTO students (name, id_group) VALUES ('Баба-Яга', 123456)");

            rs = st.executeQuery("SELECT * FROM STUDENTS");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3));
            }
        } finally {
                if (rs != null ){ rs.close(); }
                if (st != null) { st.close(); }
                if (con != null) { con.close(); }
        }
    }
}
