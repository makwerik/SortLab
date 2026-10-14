package ru.sortlab.sorting;

import ru.sortlab.collection.CustomArrayList;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * Сортировка чётных элементов
 */
public class EvenFieldSort<T> implements SortStrategy<T> {

    //какому алгоритму делегируем сортировку
    private final SortStrategy<T> delegate;

    //функция, которая получает числовое значение из объекта.
    private final ToIntFunction<T> numberExtractor;

    /**
     * Конструктор
     * @param delegate выбранный алгоритм
     * @param numberExtractor функция полчения числа
     */
    public EvenFieldSort(
            SortStrategy<T> delegate,
            ToIntFunction<T> numberExtractor
    ){
        // проверяем, что параметры не пустые
        this.delegate = Objects.requireNonNull(delegate, "Алгоритм выбранной сортировки не может быть null");
        this.numberExtractor = Objects.requireNonNull(numberExtractor,
                "Функция получения числа не может быть null");

    }

    /**
     *
     * @param element коллекция для сортировки.
     * @param comparator объект, который знает, как сравнивать два элемента
     */
    @Override
    public void sort(CustomArrayList<T> elements, Comparator<T> comparator){

        //проверка на пустоту
        Objects.requireNonNull(elements, "Коллекция не может быть null");
        Objects.requireNonNull(comparator, "Comparator не может быть null");


        //временная коллекция
        CustomArrayList<T> evenElements = new CustomArrayList<>();

        // первый цикл по исходной коллекции
        for (int i = 0; i < elements.getSize(); i++){

            T element = elements.get(i);
            // извлекаем число из объекта
            int number = numberExtractor.applyAsInt(element);

            if (number % 2 == 0){
                // добавляем объект во временную коллекцию
                evenElements.add(element);
            }
        }

        // передаю временную коллекцию выбранному алгоритму
        delegate.sort(evenElements, comparator);

        // индекс временно коллекции
        int evenIndex = 0;

        // возвращаю отсортированные объекты на исходные позиции
        for (int i = 0; i < elements.getSize(); i++){

            T element = elements.get(i);
            int number = numberExtractor.applyAsInt(element);

            if (number % 2 == 0){
                elements.set(i, evenElements.get(evenIndex));
                evenIndex++;
            }
        }
    }
}
