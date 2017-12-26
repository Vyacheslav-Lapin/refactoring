package ru.vlapin.examples.dbcp;

import lombok.SneakyThrows;
import lombok.val;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static ru.vlapin.examples.dbcp.ConnectToDBTest.LINE_SEPARATOR;
import static ru.vlapin.examples.dbcp.ConnectToDBTest.fromSystemOutPrintln;

class ConnectionPoolTest {

    @Test
    @SneakyThrows
    @DisplayName("takeConnection method works correctly")
    void takeConnection() {
        val connectionPool = new ConnectionPool();
        String s = fromSystemOutPrintln(() -> getStudent(connectionPool));
        assertThat(s, is(String.format(
                "Соединение установлено.%s1 Баба-Яга 123456", LINE_SEPARATOR)));
    }

    @SneakyThrows
    private void getStudent(ConnectionPool connectionPool){
        ConnectToDB.getStudent(connectionPool.takeConnection());
    }
}