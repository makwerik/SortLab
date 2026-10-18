package ru.sortlab.visualization;

/**
 * Наблюдатель за сортировкой.
 *
 * @FunctionalInterface означает,
 * что интерфейс содержит ровно один
 * абстрактный метод.
 *
 * Поэтому вместо отдельного класса
 * можно передать лямбда-выражение.
 */
@FunctionalInterface
public interface SortObserver {

    /**
     * Вызывается, когда алгоритм
     * выполняет очередную операцию.
     */
    void onEvent(SortEvent event);
}
