package ru.sortlab.visualization;

import ru.sortlab.collection.CustomArrayList;

import java.util.Comparator;

/**
 * Сортировка выбором с событиями.
 */
public class ObservableSelectionSort<T>
        implements ObservableSortStrategy<T> {

    @Override
    public void sort(CustomArrayList<T> elements, Comparator<T> comparator, SortObserver observer) {

        int size = elements.getSize();

        //Перемещаем границу отсортированной части.
        for (int i = 0; i < size - 1; i++) {

            /*
             * Сначала считаем,
             * что минимальный элемент
             * находится в позиции i.
             */
            int minIndex = i;

            // Ищем минимальный элемент в оставшейся части коллекции.

            for (int j = i + 1; j < size; j++) {

                observer.onEvent(new SortEvent(SortEvent.Type.COMPARE, minIndex, j));

                if (comparator.compare(elements.get(j), elements.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }

            /*
             * Если минимальный элемент
             * уже стоит правильно,
             * перестановка не нужна.
             */
            if (minIndex != i) {

                T temp = elements.get(i);

                elements.set(i, elements.get(minIndex));
                elements.set(minIndex, temp);

                observer.onEvent(new SortEvent(SortEvent.Type.UPDATE, i, minIndex));
            }
        }
    }
}
