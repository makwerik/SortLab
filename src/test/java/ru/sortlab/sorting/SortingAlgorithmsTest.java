
package ru.sortlab.sorting;

import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.comparator.CarComparators;
import ru.sortlab.model.Car;

import java.util.Comparator;

/**
 * Проверяем три алгоритма сортировки.
 *
 * Для каждого алгоритма проверяем:
 * 1. Сортировку по мощности.
 * 2. Сортировку по году.
 * 3. Сортировку по модели.
 */
public class SortingAlgorithmsTest {

    public static void main(String[] args) {

        /*
         * Создаём массив стратегий.
         *
         * Все три алгоритма реализуют
         * один интерфейс SortStrategy<Car>.
         */
        @SuppressWarnings("unchecked")
        SortStrategy<Car>[] strategies = new SortStrategy[]{
                new BubbleSort<Car>(),
                new InsertionSort<Car>(),
                new SelectionSort<Car>()
        };

        /*
         * Для каждой стратегии
         * запускаем три проверки.
         */
        for (SortStrategy<Car> strategy : strategies) {

            testSorting(
                    strategy,
                    CarComparators.byPower()
            );

            testSorting(
                    strategy,
                    CarComparators.byYear()
            );

            testSorting(
                    strategy,
                    CarComparators.byModel()
            );
        }

        System.out.println(
                "Все 9 проверок сортировки пройдены!"
        );
    }

    /**
     * Проверяет одну стратегию
     * с одним Comparator.
     */
    private static void testSorting(
            SortStrategy<Car> strategy,
            Comparator<Car> comparator
    ) {

        /*
         * Для каждой проверки создаём
         * новую коллекцию.
         *
         * Это важно: предыдущая сортировка
         * не должна влиять на следующий тест.
         */
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
                .year(2019)
                .build());

        /*
         * Выполняем сортировку.
         */
        strategy.sort(cars, comparator);

        /*
         * Проверяем каждую пару соседей.
         *
         * Если левый элемент больше правого,
         * значит сортировка выполнена неправильно.
         */
        for (int i = 0; i < cars.getSize() - 1; i++) {

            Car first = cars.get(i);
            Car second = cars.get(i + 1);

            if (comparator.compare(first, second) > 0) {

                throw new AssertionError(
                        "Ошибка сортировки: " +
                                strategy.getClass().getSimpleName()
                );
            }
        }
    }
}
