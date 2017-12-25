package ru.vlapin.examples.dbcp;

import java.sql.*;

public class ConnectToDB {
    public static void main(String... args) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("org.h2.Driver");
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

            } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null ){ rs.close(); }
                if (st != null) { st.close(); }
                if (con != null) { con.close(); }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
