
package ru.sortlab.visualization;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import ru.sortlab.model.Car;

import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Визуализация сортировки автомобилей.
 *
 * Каждый столбец показывает:
 * 1. Модель автомобиля.
 * 2. Мощность двигателя.
 * 3. Год выпуска.
 *
 * Высота столбца зависит от выбранного
 * числового поля сортировки.
 */
public class SortingVisualizer extends HBox {

    private static final double MAX_BAR_HEIGHT = 160;
    private static final double MIN_BAR_HEIGHT = 15;

    // Поле, по которому строим высоту столбцов.
    private ToIntFunction<Car> valueExtractor = Car::getPower;

    public SortingVisualizer() {

        setSpacing(8);
        setAlignment(Pos.BOTTOM_CENTER);

        getStyleClass().add("visualizer");
    }

    /**
     * Устанавливаем поле визуализации.
     */
    public void setValueExtractor(
            ToIntFunction<Car> valueExtractor
    ) {
        this.valueExtractor = valueExtractor;
    }

    /**
     * Отображаем очередной кадр.
     */
    public void showFrame(SortFrame frame) {

        getChildren().clear();

        List<Car> cars = frame.cars();

        if (cars.isEmpty()) {
            return;
        }

        // Находим максимальное значение.
        int maxValue = 1;

        for (Car car : cars) {
            maxValue = Math.max(
                    maxValue,
                    valueExtractor.applyAsInt(car)
            );
        }

        // Создаём столбцы автомобилей.
        for (int i = 0; i < cars.size(); i++) {

            Car car = cars.get(i);

            int value = valueExtractor.applyAsInt(car);

            double height = Math.max(
                    MIN_BAR_HEIGHT,
                    (double) value / maxValue * MAX_BAR_HEIGHT
            );

            // Столбец диаграммы.
            Region bar = new Region();

            bar.setPrefHeight(height);
            bar.setMinHeight(height);
            bar.setPrefWidth(48);

            bar.getStyleClass().add("sort-bar");

            // Подсветка операций.
            SortEvent event = frame.event();

            if (event != null &&
                    (i == event.firstIndex() ||
                            i == event.secondIndex())) {

                if (event.type() == SortEvent.Type.COMPARE) {

                    bar.getStyleClass().add(
                            "sort-bar-compare"
                    );

                } else {

                    bar.getStyleClass().add(
                            "sort-bar-update"
                    );
                }
            }

            // Модель автомобиля.
            Label modelLabel = new Label(
                    car.getModel()
            );

            modelLabel.getStyleClass().add(
                    "car-model-label"
            );

            // Мощность автомобиля.
            Label powerLabel = new Label(
                    car.getPower() + " л.с."
            );

            powerLabel.getStyleClass().add(
                    "car-info-label"
            );

            // Год выпуска.
            Label yearLabel = new Label(
                    String.valueOf(car.getYear())
            );

            yearLabel.getStyleClass().add(
                    "car-info-label"
            );

            // Контейнер одного автомобиля.
            VBox column = new VBox(4);

            column.setAlignment(Pos.BOTTOM_CENTER);

            column.getChildren().addAll(
                    bar,
                    modelLabel,
                    powerLabel,
                    yearLabel
            );

            HBox.setHgrow(column, Priority.ALWAYS);

            getChildren().add(column);
        }
    }
}
