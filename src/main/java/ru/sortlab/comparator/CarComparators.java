package ru.sortlab.comparator;

import ru.sortlab.model.Car;

import java.util.Comparator;

/**
 * Набор правил для сравнения автомобилей (сортировка здесь не производится),
 * определяем какой авто должен идти раньше.
 */
public final class CarComparators {

    private CarComparators(){};

    /**
     * Сравнение по мощности авто.
     */
    public static Comparator<Car> byPower(){
        return (first, second) -> Integer.compare(first.getPower(), second.getPower());
    }

    /**
     * Сравнение по году выпуска авто.
     */
    public static Comparator<Car> byYear(){
        return (first, second) -> Integer.compare(first.getYear(), second.getYear());
    }

    /**
     * Сравнение по марке.
     */
    public static Comparator<Car> byModel(){
        return (first, second) -> first.getModel().compareTo(second.getModel());
    }
}
