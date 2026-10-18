package ru.sortlab.visualization;

import ru.sortlab.collection.CustomArrayList;

import java.util.Comparator;

/**
 * Сортировка вставками с событиями.
 */
public class ObservableInsertionSort<T>
        implements ObservableSortStrategy<T> {

    @Override
    public void sort(CustomArrayList<T> elements, Comparator<T> comparator, SortObserver observer) {

        //Начинаем со второго элемента. Первый элемент сам по себе уже считается отсортированным.
        for (int i = 1; i < elements.getSize(); i++) {

            T key = elements.get(i);

            int j = i - 1;

            /*
             * Сдвигаем элементы вправо,
             * пока не найдём позицию key.
             */
            while (j >= 0) {

                observer.onEvent( new SortEvent(SortEvent.Type.COMPARE, j, j + 1));

                if (comparator.compare(elements.get(j), key) <= 0) {
                    break;
                }

                //Сдвигаем элемент вправо.

                elements.set( j + 1, elements.get(j));

                observer.onEvent(new SortEvent( SortEvent.Type.UPDATE, j,j + 1));

                j--;
            }

            //Вставляем key в найденную позицию.

            elements.set(j + 1, key);

            observer.onEvent(new SortEvent(SortEvent.Type.UPDATE, j + 1, i));
        }
    }
}
