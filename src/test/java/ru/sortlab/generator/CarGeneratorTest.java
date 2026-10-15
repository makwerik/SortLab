
package ru.sortlab.generator;

import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.model.Car;

import java.util.Random;

/**
 * Ручные тесты генератора автомобилей.
 *
 * Проверяем:
 * 1. Количество объектов.
 * 2. Корректность характеристик.
 * 3. Пустую генерацию.
 * 4. Запрет отрицательного количества.
 */
public class CarGeneratorTest {

    public static void main(String[] args) {

        /*
         * Передаём фиксированный seed.
         *
         * Благодаря этому Random будет
         * генерировать воспроизводимую
         * последовательность чисел.
         */
        CarGenerator generator =
                new CarGenerator(new Random(12345));

        /*
         * Генерируем 100 автомобилей.
         */
        CustomArrayList<Car> cars =
                generator.generate(100);

        /*
         * Проверяем размер коллекции.
         */
        if (cars.getSize() != 100) {
            throw new AssertionError(
                    "Ожидалось 100 автомобилей"
            );
        }

        /*
         * Проверяем каждый автомобиль.
         */
        for (Car car : cars) {

            if (car.getModel() == null ||
                    car.getModel().isBlank()) {

                throw new AssertionError(
                        "Сгенерирована пустая модель"
                );
            }

            if (car.getPower() < 100 ||
                    car.getPower() > 800) {

                throw new AssertionError(
                        "Мощность вне допустимого диапазона"
                );
            }

            if (car.getYear() < 1990 ||
                    car.getYear() > 2026) {

                throw new AssertionError(
                        "Год вне допустимого диапазона"
                );
            }
        }

        /*
         * Проверяем генерацию нуля объектов.
         */
        if (!generator.generate(0).isEmpty()) {
            throw new AssertionError(
                    "Коллекция должна быть пустой"
            );
        }

        /*
         * Проверяем отрицательное количество.
         */
        boolean exceptionThrown = false;

        try {
            generator.generate(-1);

        } catch (IllegalArgumentException exception) {
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            throw new AssertionError(
                    "Ожидалось исключение для count = -1"
            );
        }

        System.out.println(
                "Все тесты CarGenerator пройдены!"
        );
    }
}
