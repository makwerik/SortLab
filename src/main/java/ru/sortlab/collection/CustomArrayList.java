package ru.sortlab.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Собственная коллекция, работающая по принципу динамического массива (увеличивает вместимость по мере необходимости)
 * Т - универсальный тип данных
 */
public class CustomArrayList<T> implements Iterable<T>{

    // вместимость массива при создании
    private final static int DEFAULT_CAPACITY = 10;

    // внутрениий массив, благодаря которому можно хранить объекты разных ссылочных типов
    private Object[] elements;

    // кол-во реально добавленных элементов
    private int size;

    public CustomArrayList(){

        // создаю массив на 10 элементов
        elements = new Object[DEFAULT_CAPACITY];

        // коллекция пустая
        size = 0;
    }

    /**
     * Метод для добавления элемента в коллекцию.
     * @param element объект, который добавляем
     */
    public void add(T element){

        //проверяем, хватает ли места
        if (size == elements.length){
            grow();
        }

        // записываем объект в следующую свободную ячейку
        elements[size++] = element;
    }

    /**
     * Метод увеличения вместимости массива
     */
    private void grow(){
        int newCapacity = elements.length * 2;

        // создание нового массива
        Object[] newElements = new Object[newCapacity];

        // переносим все существующие элементы
        for (int i = 0; i < size; i++){;
            newElements[i] = elements[i];

        };

        elements = newElements;
    }

    /**
     * Метод для получения элемента по индексу
     * @param index индекс
     * @return Объект элемента
     */
    @SuppressWarnings("unchecked")
    public T get(int index){

        // проверяем индекс
        checkIndex(index);

        // (T) означает приведение типа.
        return (T) elements[index];
    }

    /**
     * Метод замены элемента по индексу
     * @param index индекс
     * @param element объект на который заменяем элемент
     */
    public void set(int index, T element){

        // проверка индекса
        checkIndex(index);

        elements[index] = element;
    }

    /**
     * Метод для удаления элемента по индексу
     * @param index индекс
     * @return удалённый элемент
     */
    @SuppressWarnings("unchecked")
    public T remove(int index){

        // проверка индекса
        checkIndex(index);

        // Запонимаем удаляемый объект
        T removedElement = (T) elements[index];

        // сдвигаем все следующие элементы на одну позицию влево
        for(int i = index; i < size - 1; i++){
            elements[i] = elements[i + 1];
        }

        // уменьшаем кол-во элементов
        elements[--size] = null;

        return removedElement;
    }

    /**
     * Метод проверки индекса
     * @param index проверяемый индекс
     */
    private void checkIndex(int index){
        if (index < 0 || index >= size){
            throw new IndexOutOfBoundsException("Индекс: " + index + ", размер коллекции: " + size);
        }
    }

    /**
     * Метод возвращает кол-во элементов
     * @return кол-во элементов в коллекции
     */
    public int getSize(){
        return size;
    }

    /**
     * Метод проверят пустая ли коллекция
     * @return true - пустая, false - есть элементы
     */
    public boolean isEmpty(){
        return size == 0;
    }

    /**
     * Метод полной очистки коллекции
     */
    public void clear(){
        for (int i = 0; i < size; i++){
            elements[i] = null;
        }

        size = 0;
    }

    /**
     * Метод возвращающий итератор, который позволяет последовательно обходить коллекцию
     */
    @Override
    public Iterator<T> iterator(){

        // создаем анонимный класс
        return new Iterator<T>() {

            // позиция следующего эллемента
            private int cursor = 0;


            @Override
            public boolean hasNext() {
                // проверяем остались ли элементы
                return cursor < size;
            }

            @Override
            public T next() {
                // возвращаем следующий элемент
                if (!hasNext()){
                    throw new NoSuchElementException();
                }

                // получаем элемент и сдвигаем курсор
                return get(cursor++);
            }
        };
    }


    /**
     * Преобразует коллекцию в Stream.
     * Не превращает CustomArrayList
     * в стандартный ArrayList.
     *
     * StreamSupport использует механизм
     * перебора элементов нашей коллекции.
     *
     * spliterator() доступен благодаря
     * интерфейсу Iterable<T>.
     *
     * false означает последовательный Stream,
     * а не параллельный.
     */
    public Stream<T> stream(){
        return StreamSupport.stream(this.spliterator(), false);
    }

}
