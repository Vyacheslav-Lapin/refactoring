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

            //определить сумму для каждой строки
            double thisAmount = each.amountFor();

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
