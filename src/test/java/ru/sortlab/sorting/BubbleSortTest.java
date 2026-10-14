
package ru.sortlab.sorting;

import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.comparator.CarComparators;
import ru.sortlab.model.Car;

/**
 * Ручной тест пузырьковой сортировки.
 *
 * Создаём автомобили, сортируем их
 * и проверяем результат.

 */
public class BubbleSortTest {

    public static void main(String[] args) {

        CustomArrayList<Car> cars = new CustomArrayList<>();

        cars.add(new Car.Builder()
                .model("BMW")
                .power(600)
                .year(2020)
                .build());

        cars.add(new Car.Builder()
                .model("Toyota")
                .power(340)
                .year(2021)
                .build());

        cars.add(new Car.Builder()
                .model("Audi")
                .power(630)
                .year(2023)
                .build());

        SortStrategy<Car> strategy = new BubbleSort<>();

        strategy.sort(cars, CarComparators.byPower());

        /*
         * После сортировки ожидаем:
         *
         * 340, 600, 630.
         *
         * Если порядок неправильный,
         * выбрасываем AssertionError.
         */
        if (cars.get(0).getPower() != 340 ||
                cars.get(1).getPower() != 600 ||
                cars.get(2).getPower() != 630) {

            throw new AssertionError(
                    "Ошибка сортировки по мощности!"
            );
        }

        System.out.println("Тест BubbleSort пройден!");
    }
}
