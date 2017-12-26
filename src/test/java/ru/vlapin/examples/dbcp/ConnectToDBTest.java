package ru.vlapin.examples.dbcp;

import lombok.SneakyThrows;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ConnectToDBTest {

    static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @SneakyThrows
    public static String fromSystemOutPrint(Runnable runnable) {

        PrintStream realOut = System.out;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(out)) {

            System.setOut(printStream);
            runnable.run();

            return new String(out.toByteArray());

        } finally {
            System.setOut(realOut);
        }
    }

    public static String fromSystemOutPrintln(Runnable runnable) {
        String s = fromSystemOutPrint(runnable);
        if (s.endsWith(LINE_SEPARATOR))
            s = s.substring(0, s.length() - LINE_SEPARATOR.length());
        return s;
    }

    @Test
    @DisplayName("Main method works correctly")
    void main() {
        String s = fromSystemOutPrintln(ConnectToDB::main);
        assertThat(s, Is.is("Соединение установлено." + LINE_SEPARATOR + "1 Баба-Яга 123456"));
    }
}