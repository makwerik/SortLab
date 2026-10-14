package ru.sortlab.sorting;
import ru.sortlab.collection.CustomArrayList;
import java.util.Comparator;

/**
 * Класс пузырьковой сортировки и реализует интерфейс SortStrategy
 */
public class BubbleSort<T> implements SortStrategy<T> {


    @Override
    public void sort(CustomArrayList<T> elements, Comparator<T> comparator){

        // получаем кол-во элементов
        int n = elements.getSize();

        for (int i = 0; i < n - 1; i++){

            // флаг swapped показывает были ли перестановки,
            // если за проход ничего не поменялось - коллекция отсортирована
            boolean swapped = false;

            // внутренний цикл
            // j - индекс текущего элемента
            // j + 1 - индекс следующего элемента

            for (int j = 0; j < n - 1; j++){

                // Получаем два соседних элемента
                T first = elements.get(j);
                T second = elements.get(j + 1);

                //comparator.compare(first, second) Возвращает:
                //Отрицательное число:
                //first должен идти раньше second.

                //Ноль:
                //объекты равны по выбранному полю.

                //Положительное число:
                //first должен идти после second.

                //Если результат > 0,
                //меняем элементы местами.
                if (comparator.compare(first, second) > 0){
                    // меняю элементы местами, через метод моей коллекции
                    elements.set(j, second);
                    elements.set(j + 1, first);

                    swapped = true;
                }
            }

            if (!swapped){
                break;
            }
        }
    }
}
