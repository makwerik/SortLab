package ru.sortlab.visualization;

import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.sorting.SortStrategy;

import java.util.Comparator;

/**
 * Расширяет обычный интерфейс сортировки.
 *
 * Помимо сортировки, алгоритм умеет уведомлять наблюдателя о своих шагах.
 */
public interface ObservableSortStrategy<T> extends SortStrategy<T> {

    /**
     * Сортировка с наблюдателем.
     */
    void sort(CustomArrayList<T> elements, Comparator<T> comparator, SortObserver observer);

    /**
     * Реализация обычного метода из SortStrategy.
     * Если наблюдатель не передан, события просто игнорируются.
     */
    @Override
    default void sort(CustomArrayList<T> elements, Comparator<T> comparator) {

        sort(elements, comparator, event -> {});
    }
}
