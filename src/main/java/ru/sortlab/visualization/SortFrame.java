
package ru.sortlab.visualization;

import ru.sortlab.model.Car;

import java.util.List;

/**
 * SortFrame — один кадр анимации.
 *
 * values — значения всех столбцов.
 *
 * event — операция, которая произошла
 * перед созданием кадра.
 *
 * List.copyOf создаёт неизменяемую копию,
 * чтобы сохранённый кадр не изменился.
 */
public record SortFrame(List<Car> cars, SortEvent event) {

    public SortFrame {
         cars = List.copyOf(cars);
    }
}
