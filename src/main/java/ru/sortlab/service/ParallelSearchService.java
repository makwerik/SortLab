
package ru.sortlab.service;

import ru.sortlab.collection.CustomArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * ParallelSearchService — многопоточный
 * подсчёт элементов коллекции.
 *
 * <T> означает, что сервис универсален.
 *
 * Он может работать с:
 *
 * CustomArrayList<Car>
 * CustomArrayList<String>
 * CustomArrayList<Integer>
 *
 * Сервис не зависит от конкретного
 * класса Car.
 */
public class ParallelSearchService<T> {

    /**
     * Подсчитывает количество элементов,
     * соответствующих переданному условию.
     *
     * @param elements исходная коллекция
     * @param condition условие поиска
     * @param threadCount количество потоков
     *
     * @return количество совпадений
     */
    public long countMatches(
            CustomArrayList<T> elements,
            Predicate<T> condition,
            int threadCount
    ) throws InterruptedException, ExecutionException {


        Objects.requireNonNull(
                elements,
                "Коллекция не может быть null"
        );

        Objects.requireNonNull(
                condition,
                "Условие поиска не может быть null"
        );

        if (threadCount <= 0) {
            throw new IllegalArgumentException(
                    "Количество потоков должно быть больше нуля"
            );
        }


        int size = elements.getSize();

        if (size == 0) {
            return 0;
        }

        //Не создаём больше рабочих потоков, чем имеется элементов.

        int workers = Math.min(threadCount, size);

        // вычисляем сколько задач отдаём 1 потоку
        int chunkSize = size / workers;

        //Этот дополнительный элемент распределим одному из потоков.
        int remainder = size % workers;

        // Создаём пул потоков.
        ExecutorService executor =
                Executors.newFixedThreadPool(workers);


        //результаты задач.
        List<Future<Long>> futures =
                new ArrayList<>();

        try {

            //start — начало очередного диапазона.

            int start = 0;

            //Создаём задачу для каждого потока.

            for (int i = 0; i < workers; i++) {

                // первый поток получит остаток задач из remainder
                int currentChunkSize =
                        chunkSize + (i < remainder ? 1 : 0);

                //Вычисляем конец диапазона.

                int end = start + currentChunkSize;

                //Фиксируем границы диапазона для использования внутри лямбды.

                final int fromIndex = start;
                final int toIndex = end;

                /*
                 * Создаём задачу Callable<Long>.
                 *
                 * Она проверит только свою
                 * часть коллекции.
                 */
                Callable<Long> task = () -> {

                    /*
                     * Локальный счётчик.
                     *
                     * У каждой задачи собственная
                     * переменная localCount.
                     *
                     * Поэтому потоки не изменяют
                     * один общий счётчик.
                     */
                    long localCount = 0;

                    /*
                     * Проверяем элементы
                     * своего диапазона.
                     */
                    for (int j = fromIndex; j < toIndex; j++) {

                        T element = elements.get(j);

                        /*
                         * condition.test(element)
                         * проверяет условие поиска.
                         */
                        if (condition.test(element)) {
                            localCount++;
                        }
                    }

                    //Возвращаем количество
                    return localCount;
                };

                /*
                 * Отправляем задачу в пул потоков.
                 *
                 * submit() возвращает Future,
                 * через который получим результат.
                 */
                Future<Long> future =
                        executor.submit(task);

                futures.add(future);

                //Следующий диапазон начинается там, где закончился предыдущий.

                start = end;
            }

            // общий результат
            long totalCount = 0;

            //Получаем результаты всех задач.

            for (Future<Long> future : futures) {

                //get() ожидает завершения задачи и возвращает её результат.
                totalCount += future.get();
            }

            return totalCount;

        } finally {

            // Останавливаем приём новых задач.
            executor.shutdown();

            // Если выполнение было прервано, отменяем оставшиеся задачи.

            if (Thread.currentThread().isInterrupted()) {
                executor.shutdownNow();
            }
        }
    }
}
