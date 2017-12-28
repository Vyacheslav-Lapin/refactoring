package ru.vlapin.examples.first;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class CustomerTest {

    @Test
    @DisplayName("statement method works correctly")
    void statement() {
        Customer customer = new Customer("Вася");
        customer.addRental(
                new Rental(
                        new Movie("Man in Black", Movie.REGULAR),
                        2));
        assertThat(customer.statement(), is(
                "Учет аренды для Вася\n\t" +
                "Man in Black\t2.0\n" +
                "Сумма задолженности составляет 2.0\n" +
                "Вы заработали 1 очков за активность"));
    }
}