package ru.vlapin.examples.first;

import lombok.Value;

@Value
public class Rental {
    private Movie movie;
    private int daysRented;
}
