package ru.vlapin.examples.first;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Customer {
    @Getter
    private final String name;
    private List<Rental> rentals = new ArrayList<>();

    public void addRental(Rental arg) {
        rentals.add(arg);
    }

    public String statement() {
        double totalAmount = 0;
        int frequentRenterPoints = 0;
        String result = "Учет аренды для " + getName() + "\n";
        for (Rental each : rentals) {
            double thisAmount = 0;

            //определить сумму для каждой строки
            switch (each.getMovie().getPriceCode()) {
                case Movie.REGULAR:
                    thisAmount += 2;
                    if (each.getDaysRented() > 2)
                        thisAmount += (each.getDaysRented() - 2) * 1.5;
                    break;
                case Movie.NEW_RELEASE:
                    thisAmount += each.getDaysRented() * 3;
                    break;
                case Movie.CHILDRENS:
                    thisAmount += 1.5;
                    if (each.getDaysRented() > 3)
                        thisAmount += (each.getDaysRented() - 3) * 1.5;
                    break;
            }

            // добавить очки для активного арендатора
            frequentRenterPoints++;

            // бонус за аренду новинки на два дня
            if ((each.getMovie().getPriceCode() == Movie.NEW_RELEASE) &&
                    each.getDaysRented() > 1) frequentRenterPoints++;

            //показать результаты для этой аренды
            result += "\t" + each.getMovie().getTitle() + "\t" +
                    String.valueOf(thisAmount) + "\n";
            totalAmount += thisAmount;
        }

        //добавить нижний колонтитул
        result += "Сумма задолженности составляет " + totalAmount + "\n";

        result += "Вы заработали " + frequentRenterPoints + " очков за активность";

        return result;
    }
}
