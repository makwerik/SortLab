
package ru.sortlab.sorting;

import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.comparator.CarComparators;
import ru.sortlab.model.Car;

/**
 * Ручные тесты дополнительной сортировки.
 *
 * Проверяем:
 * 1. Все три алгоритма.
 * 2. Сохранение нечётных позиций.
 * 3. Правильный порядок чётных значений.
 */
public class EvenFieldSortTest {

    public static void main(String[] args) {

        test(new BubbleSort<>());
        test(new InsertionSort<>());
        test(new SelectionSort<>());

        System.out.println(
                "Все тесты сортировки чётных пройдены!"
        );
    }

    /**
     * Проверяем одну выбранную стратегию.
     */
    private static void test(SortStrategy<Car> strategy) {

        CustomArrayList<Car> cars = new CustomArrayList<>();

        cars.add(createCar("BMW", 600));
        cars.add(createCar("Audi", 351));
        cars.add(createCar("Toyota", 340));
        cars.add(createCar("Mercedes", 475));
        cars.add(createCar("Porsche", 520));

        /*
         * Запоминаем ссылки на объекты
         * с нечётной мощностью.
         *
         * Именно эти объекты должны остаться
         * на прежних позициях.
         */
        Car oddFirst = cars.get(1);
        Car oddSecond = cars.get(3);

        EvenFieldSort<Car> evenSort =
                new EvenFieldSort<>(
                        strategy,
                        Car::getPower
                );

        evenSort.sort(cars, CarComparators.byPower());

        /*
         * Проверяем порядок чётных значений.
         */
        if (cars.get(0).getPower() != 340 ||
                cars.get(2).getPower() != 520 ||
                cars.get(4).getPower() != 600) {

            throw new AssertionError(
                    "Чётные элементы отсортированы неправильно"
            );
        }

        /*
         * Проверяем, что нечётные объекты
         * остались на исходных позициях.
         *
         * == для объектов проверяет
         * идентичность ссылок.
         *
         * Здесь нам важно, чтобы это были
         * именно те же самые объекты.
         */
        if (cars.get(1) != oddFirst ||
                cars.get(3) != oddSecond) {

            throw new AssertionError(
                    "Нечётные элементы изменили позиции"
            );
        }

        System.out.println(
                strategy.getClass().getSimpleName() +
                        ": тест пройден"
        );
    }

    /**
     * Вспомогательный метод создания автомобиля.
     *
     * Он нужен, чтобы не повторять Builder
     * в каждой строке теста.
     */
    private static Car createCar(String model, int power) {

        return new Car.Builder()
                .model(model)
                .power(power)
                .year(2020)
                .build();
    }
}
