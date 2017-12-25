package ru.vlapin.examples.first;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

class CustomerTest {

    @Test
    @DisplayName("statement method works correctly")
    void statement() {
        Customer customer = new Customer("Вася");
        assertThat(customer.statement(), Is.is("Учет аренды для Вася\n" +
                "Сумма задолженности составляет 0.0\n" +
                "Вы заработали 0 очков за активность"));
    }
}