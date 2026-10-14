package ru.sortlab.sorting;

import ru.sortlab.collection.CustomArrayList;
import java.util.Comparator;


public interface SortStrategy<T> {

    /**
     * Метод сортировки
      * @param elements коллекция для сортировки.
     * @param comparator объект, который знает, как сравнивать два элемента
     */
    void sort(CustomArrayList<T> elements, Comparator<T> comparator);

}