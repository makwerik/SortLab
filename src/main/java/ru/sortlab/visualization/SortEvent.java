package ru.sortlab.visualization;

/**
 * SortEvent описывает один шаг сортировки.
 *
 * Тип события:
 * COMPARE — сравнение двух элементов.
 * UPDATE — изменение расположения элементов.
 *
 * firstIndex и secondIndex — индексы
 * элементов, участвующих в операции.
 */

public record SortEvent(Type type, int firstIndex, int secondIndex) {
    // record нужен для небольших неизменяемых объектов, сам создаёт конструктор и методы доступа

    public enum Type {COMPARE,UPDATE}
}
