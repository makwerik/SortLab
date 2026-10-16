
package ru.sortlab.service;

import ru.sortlab.collection.CustomArrayList;

import java.util.concurrent.ExecutionException;

/**
 * Ручные тесты многопоточного поиска.
 *
 * Проверяем:
 * 1. Подсчёт совпадений.
 * 2. Разное количество потоков.
 * 3. Пустую коллекцию.
 * 4. Некорректное количество потоков.
 */
public class ParallelSearchServiceTest {

    public static void main(String[] args)
            throws InterruptedException, ExecutionException {

        /*
         * Создаём коллекцию целых чисел.
         *
         * Здесь не используем Car,
         * чтобы показать универсальность
         * нашего сервиса.
         */
        CustomArrayList<Integer> numbers =
                new CustomArrayList<>();

        /*
         * Добавляем числа:
         *
         * 1, 2, 3, 2, 4, 2, 5, 2
         */
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(2);
        numbers.add(4);
        numbers.add(2);
        numbers.add(5);
        numbers.add(2);

        /*
         * Создаём сервис.
         */
        ParallelSearchService<Integer> service =
                new ParallelSearchService<>();

        /*
         * Проверяем подсчёт числа 2.
         *
         * Оно встречается четыре раза.
         */
        long count = service.countMatches(
                numbers,
                number -> number == 2,
                3
        );

        if (count != 4) {
            throw new AssertionError(
                    "Ожидалось 4 совпадения, получено " + count
            );
        }

        /*
         * Проверяем тот же поиск
         * с одним потоком.
         */
        long singleThreadCount = service.countMatches(
                numbers,
                number -> number == 2,
                1
        );

        if (singleThreadCount != 4) {
            throw new AssertionError(
                    "Ошибка однопоточного подсчёта"
            );
        }

        /*
         * Проверяем больше потоков,
         * чем элементов.
         */
        long manyThreadsCount = service.countMatches(
                numbers,
                number -> number == 2,
                20
        );

        if (manyThreadsCount != 4) {
            throw new AssertionError(
                    "Ошибка при большом количестве потоков"
            );
        }

        /*
         * Проверяем пустую коллекцию.
         */
        CustomArrayList<Integer> empty =
                new CustomArrayList<>();

        long emptyCount = service.countMatches(
                empty,
                number -> number == 2,
                4
        );

        if (emptyCount != 0) {
            throw new AssertionError(
                    "Пустая коллекция должна давать 0"
            );
        }

        /*
         * Проверяем некорректное число потоков.
         */
        boolean exceptionThrown = false;

        try {

            service.countMatches(
                    numbers,
                    number -> number == 2,
                    0
            );

        } catch (IllegalArgumentException exception) {

            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            throw new AssertionError(
                    "Ожидалось исключение для threadCount = 0"
            );
        }

        System.out.println(
                "Все тесты ParallelSearchService пройдены!"
        );
    }
}
