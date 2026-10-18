package ru.sortlab.visualization;

import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.model.Car;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Сервис подготовки кадров анимации.
 * Его задача — получить шаги сортировки
 * и сохранить состояние коллекции
 * после каждой операции.
 */
public class SortVisualizationService {

    /**
     * Создаёт последовательность кадров.
     *
     * @param elements коллекция элементов
     * @param comparator правило сравнения
     * @param strategy наблюдаемый алгоритм
     *
     * @return список кадров анимации
     */
    public List<SortFrame> createFrames(CustomArrayList<Car> elements, Comparator<Car> comparator,
                                        ObservableSortStrategy<Car> strategy) {

        //Список кадров.

        List<SortFrame> frames = new ArrayList<>();

        /*
         * Создаём начальный кадр.
         *
         * event = null означает,
         * что операция ещё не выполнялась.
         */
        frames.add(new SortFrame(copyCars(elements), null));

        /*
         * Запускаем реальный алгоритм.
         *
         * При каждом событии алгоритм
         * вызовет нашу лямбду.
         */
        strategy.sort(elements, comparator, event -> {
                    //Создаём новый кадр с текущим состоянием коллекции.

                    frames.add(new SortFrame(copyCars(elements), event));
                }
        );

        return frames;
    }

    /**
     * Копируем текущий порядок автомобилей.
     */
    private List<Car> copyCars(CustomArrayList<Car> elements) {

        List<Car> result = new ArrayList<>();

        for (Car car : elements) {
            result.add(car);
        }

        return result;
    }
}
