
package ru.sortlab.visualization;

import ru.sortlab.collection.CustomArrayList;

import java.util.Comparator;

/**
 * Пузырьковая сортировка с событиями.
 * При сравнении двух элементов отправляется событие COMPARE.
 * При перестановке элементов отправляется событие UPDATE.
 */
public class ObservableBubbleSort<T> implements ObservableSortStrategy<T> {

    @Override
    public void sort(CustomArrayList<T> elements,Comparator<T> comparator, SortObserver observer) {

        int size = elements.getSize();

        //Внешний цикл определяет, сколько проходов выполнено.

        for (int i = 0; i < size - 1; i++) {

            boolean swapped = false;

            /*
             * С каждым проходом последний
             * элемент оставшегося диапазона
             * занимает правильную позицию.
             */
            for (int j = 0; j < size - i - 1; j++) {

                //Уведомляем о сравнении.
                observer.onEvent(new SortEvent(SortEvent.Type.COMPARE, j,j + 1));

                //Сравниваем соседние элементы.

                if (comparator.compare( elements.get(j), elements.get(j + 1)) > 0) {

                    //Меняем элементы местами.
                    T temp = elements.get(j);

                    elements.set(j, elements.get(j + 1));
                    elements.set(j + 1, temp);

                    swapped = true;

                    //Уведомляем о перестановке.
                    observer.onEvent(new SortEvent(SortEvent.Type.UPDATE, j, j + 1));
                }
            }

            /*
             * Если за проход не было
             * перестановок, коллекция
             * уже отсортирована.
             */
            if (!swapped) {
                break;
            }
        }
    }
}
