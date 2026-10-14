
package ru.sortlab.service;

import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.sorting.SortStrategy;

import java.util.Comparator;
import java.util.Objects;

/**
 * Сервис управления сортировкой.
 *
 * Его задача — хранить выбранную стратегию
 * и запускать сортировку.
 *
 * Сам сервис НЕ знает, как работает
 * Bubble Sort, Insertion Sort или Selection Sort.
 *
 * Он знает только общий интерфейс SortStrategy.
 */
public class SortingService<T> {

    //Текущая стратегия сортировки.
    //Например, здесь может находиться объект BubbleSort<Car>.

    private SortStrategy<T> strategy;

    /**
     * Конструктор сервиса.
     *
     * При создании мы сразу передаём
     * алгоритм, который хотим использовать.
     */
    public SortingService(SortStrategy<T> strategy) {

        /*
         * requireNonNull проверяет,
         * что стратегия не равна null.
         *
         * Иначе выбрасывается исключение.
         */
        this.strategy = Objects.requireNonNull(
                strategy,
                "Стратегия сортировки не может быть null"
        );
    }

    /**
     * Изменение алгоритма сортировки.
     *
     * Например:
     *
     * service.setStrategy(new SelectionSort<>());
     *
     * После этого сервис будет использовать
     * Selection Sort вместо прежнего алгоритма.
     */
    public void setStrategy(SortStrategy<T> strategy) {

        this.strategy = Objects.requireNonNull(
                strategy,
                "Стратегия сортировки не может быть null"
        );
    }

    /**
     * Запуск сортировки.
     *
     * @param elements коллекция элементов
     * @param comparator правило сравнения
     */
    public void sort(
            CustomArrayList<T> elements,
            Comparator<T> comparator
    ) {

        /*
         * Проверяем входные параметры.
         */
        Objects.requireNonNull(
                elements,
                "Коллекция не может быть null"
        );

        Objects.requireNonNull(
                comparator,
                "Comparator не может быть null"
        );

        /*
         * Делегируем сортировку
         * выбранной стратегии.
         *
         * Делегировать — значит поручить
         * выполнение работы другому объекту.
         */
        strategy.sort(elements, comparator);
    }
}
