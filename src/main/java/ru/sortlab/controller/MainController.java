package ru.sortlab.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.comparator.CarComparators;
import ru.sortlab.generator.CarGenerator;
import ru.sortlab.io.CarFileReader;
import ru.sortlab.io.CarFileWriter;
import ru.sortlab.model.Car;
import ru.sortlab.service.SortingService;
import ru.sortlab.sorting.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;

import javafx.scene.layout.StackPane;
import javafx.scene.control.Slider;

import ru.sortlab.visualization.*;
import ru.sortlab.visualization.SortEvent;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Контроллер главного окна, для обработки действий пользователя
 */
public class MainController {

    // связываю поле с элементом FXML
    @FXML
    private ComboBox<String> algorithmComboBox;

    @FXML
    private ComboBox<String> fieldComboBox;

    @FXML
    private TextField countTextField;

    @FXML
    private TableView<Car> carTable;

    @FXML
    private TableColumn<Car, String> modelColumn;

    @FXML
    private TableColumn<Car, Integer> powerColumn;

    @FXML
    private TableColumn<Car, Integer> yearColumn;

    @FXML
    private Label countLabel;

    @FXML
    private Label statusLabel;

    //Контейнер для визуализатора.
    @FXML
    private StackPane visualizationContainer;

    //Слайдер скорости анимации.
    @FXML
    private Slider speedSlider;

    //Счётчики операций.
    @FXML
    private Label comparisonLabel;

    @FXML
    private Label swapLabel;

    //Визуальный компонент.
    private final SortingVisualizer visualizer = new SortingVisualizer();

    //Проигрыватель кадров.
    private final SortAnimationPlayer animationPlayer = new SortAnimationPlayer();

    //Сервис подготовки кадров.
    private final SortVisualizationService visualizationService = new SortVisualizationService();

    //Сохранённые кадры последней анимации.
    private List<SortFrame> animationFrames = List.of();

    // собственная коллекция
    private CustomArrayList<Car> cars = new CustomArrayList<>();

    // коллекция для отображения данных в интерфейсе, нужна только для таблицы
    private final ObservableList<Car> tableItems = FXCollections.observableArrayList();

    // генератор случайных авто
    private final CarGenerator generator = new CarGenerator();

    /**
     * Метод вызывается автоматически после загрузки FXML (настройка элементов интерфейса)
     */
    @FXML
    private void initialize(){

        // варианты алгоритмов
        algorithmComboBox.getItems().addAll("Bubble Sort", "Insertion Sort", "Selection Sort");
        algorithmComboBox.setValue("Bubble Sort");

        // варианты полей
        fieldComboBox.getItems().addAll("Модель", "Мощность", "Год выпуска");
        fieldComboBox.setValue("Мощность");

        // настройка столбца модели (для каждого объекта авто возвращаем его название)
        modelColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getModel()
        ));
        powerColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(
                cell.getValue().getPower()
        ));
        yearColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(
                cell.getValue().getYear()
        ));

        // передача данных в таблицу
        carTable.setItems(tableItems);

        // столбцам можно использовать доступную ширину таблицы
        carTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //Добавляем визуализатор в контейнер.
        visualizationContainer.getChildren().add(visualizer);

        refreshTable();
    }

    /**
     * Обработчик кнопки "Сгенерировать"
     */
    @FXML
    private void handleGenerate(){

        try {

            // читаем кол-во авто из countTextField
            int count = Integer.parseInt(countTextField.getText().trim());

            // ограничиваю кол-во чтобы не перенагружать интерфейс
            if (count < 1 || count > 10000){
                throw new IllegalArgumentException("Количество автомобилей должно быть от 1 до 10 000");
            }

            cars = generator.generate(count);

            refreshTable();

            setStatus("Сгенерировано автомобилей: " + count);

        } catch (NumberFormatException exception){
            showError("Введите корректное целое количество автомобилей");

        } catch (IllegalArgumentException exception){
            showError(exception.getMessage());
        }
    }

    /**
     * Загрузка автомобилей из файла
     */
    @FXML
    private void handleLoadFile(){

        // окно выбора файла
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите CSV файл автомобилей");

        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        // открыаваем окно выбора файла
        File file = chooser.showOpenDialog(null);

        if (file == null){
            return;
        }

        try {
            CustomArrayList<Car> loadedCars = CarFileReader.read(file.toPath());

            // заменяю текущую коллекцию только после успешного чтения
            cars = loadedCars;

            refreshTable();

            setStatus("Загружен файл: " + file.getName());

        }catch (IOException | IllegalArgumentException exception){
            showError("Не удалось загрузить файл:\n" + exception);
        }
    }


    /**
     * Ручное добавление автомобиля.
     */
    @FXML
    private void handleAddCar() {

        //Запрашиваем модель.

        TextInputDialog modelDialog = new TextInputDialog();

        modelDialog.setTitle("Добавить автомобиль");
        modelDialog.setHeaderText("Введите модель");
        modelDialog.setContentText("Модель:");

        String model = modelDialog.showAndWait().orElse(null);

        if (model == null) {
            return;
        }

        // мощность
        TextInputDialog powerDialog = new TextInputDialog();

        powerDialog.setTitle("Добавить автомобиль");
        powerDialog.setHeaderText("Введите мощность");
        powerDialog.setContentText("Мощность:");

        String powerText = powerDialog.showAndWait().orElse(null);

        if (powerText == null) {
            return;
        }

        // год выпуска
        TextInputDialog yearDialog = new TextInputDialog();

        yearDialog.setTitle("Добавить автомобиль");
        yearDialog.setHeaderText("Введите год");
        yearDialog.setContentText("Год:");

        String yearText = yearDialog.showAndWait().orElse(null);

        if (yearText == null) {
            return;
        }

        try {

            // Проверяем данные через CarValidator.

            Car car = ru.sortlab.validation.CarValidator.parse(
                    model,
                    powerText,
                    yearText
            );

            //Добавляем автомобиль в коллекцию.
            cars.add(car);

            refreshTable();

            setStatus("Добавлен автомобиль: " + car.getModel());

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Запуск сортировки.
     *
     * Если автомобилей не более 50 и выбрано
     * числовое поле, запускаем анимацию.
     *
     * Для остальных случаев выполняем
     * обычную сортировку без анимации.
     */
    @FXML
    private void handleSort() {

        if (cars.isEmpty()) {
            showError("Сначала загрузите или создайте автомобили.");
            return;
        }

        // Останавливаем предыдущую анимацию.
        animationPlayer.stop();

        String selectedField = fieldComboBox.getValue();

        // Проверяем, поддерживается ли анимация.
        boolean canAnimate = cars.getSize() <= 50 && ("Мощность".equals(selectedField) || "Год выпуска".equals(selectedField));

        if (canAnimate) {

            // Запускаем анимацию автоматически.
            handleStartAnimation();

        } else {

            // Обычная сортировка без анимации.
            SortStrategy<Car> strategy = createStrategy();

            Comparator<Car> comparator = createComparator();

            SortingService<Car> service = new SortingService<>(strategy);

            service.sort(cars, comparator);

            refreshTable();

            setStatus("Сортировка завершена: " + algorithmComboBox.getValue() + " по полю " + selectedField);
        }
    }


    /**
     * Сохранение результата.
     */
    @FXML
    private void handleSave() {

        // Проверяем, есть ли автомобили.
        if (cars.isEmpty()) {
            showError("Нет автомобилей для сохранения.");
            return;
        }

        // Создаём окно сохранения файла.
        FileChooser chooser = new FileChooser();

        chooser.setTitle("Сохранить результат сортировки");

        // Разрешаем сохранять текстовые файлы.
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовый файл", "*.txt"));

        // Предлагаем имя файла.
        chooser.setInitialFileName("sortlab-results.txt");

        // Получаем окно приложения через statusLabel.
        // Не используем carTable, поскольку его getScene()
        // в текущей конфигурации возвращает null.
        File file = chooser.showSaveDialog(statusLabel.getScene().getWindow());

        // Пользователь отменил сохранение.
        if (file == null) {
            return;
        }

        try {

            // Дописываем результаты сортировки в файл.
            CarFileWriter.append(file.toPath(), cars, "Результат сортировки SortLab"
            );

            // Показываем успешное сохранение.
            setStatus("Результат сохранён: " + file.getName());

        } catch (IOException exception) {

            showError("Ошибка сохранения: " + exception.getMessage());
        }
    }


    /**
     * Создание выбранной стратегии.
     */
    private SortStrategy<Car> createStrategy() {

        String selected = algorithmComboBox.getValue();

        return switch (selected) {

            case "Insertion Sort" -> new InsertionSort<>();

            case "Selection Sort" -> new SelectionSort<>();

            default -> new BubbleSort<>();
        };
    }

    /**
     * Создание Comparator для выбранного поля.
     */
    private Comparator<Car> createComparator() {

        String selected = fieldComboBox.getValue();

        return switch (selected) {

            case "Модель" -> CarComparators.byModel();

            case "Год выпуска" -> CarComparators.byYear();

            default -> CarComparators.byPower();
        };
    }

    /**
     * Обновление таблицы.
     *
     * Мы очищаем ObservableList,
     * а затем копируем в неё автомобили
     * из нашей CustomArrayList.
     *
     * Алгоритмы сортировки работают
     * только с CustomArrayList.
     */
    private void refreshTable() {

        tableItems.clear();

        for (Car car : cars) {
            tableItems.add(car);
        }

        countLabel.setText(
                cars.getSize() + " элементов"
        );
    }

    /**
     * Изменение строки состояния.
     */
    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Отображение сообщения об ошибке.
     */
    private void showError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Ошибка SortLab");
        alert.setHeaderText("Не удалось выполнить действие");
        alert.setContentText(message);

        alert.showAndWait();
    }


    /**
     * Запуск анимации сортировки.
     */
    @FXML
    private void handleStartAnimation() {

        if (cars.isEmpty()) {

            showError(
                    "Сначала создайте или загрузите автомобили."
            );

            return;
        }

        /*
         * Чтобы интерфейс не перегружался,
         * ограничиваем число столбцов.
         *
         * Большие коллекции продолжают
         * поддерживаться обычной сортировкой.
         */
        if (cars.getSize() > 50) {

            showError(
                    "Для анимации используйте не более 50 автомобилей."
            );

            return;
        }

        /*
         * Определяем числовое поле,
         * которое будем визуализировать.
         */
        ToIntFunction<Car> valueExtractor;

        String selectedField = fieldComboBox.getValue();

        if ("Мощность".equals(selectedField)) {

            valueExtractor = Car::getPower;

        } else if ("Год выпуска".equals(selectedField)) {

            valueExtractor = Car::getYear;

        } else {

            showError("Анимация доступна только для мощности и года выпуска.");

            return;
        }

        /*
         * Создаём наблюдаемый алгоритм.
         */
        ObservableSortStrategy<Car> strategy = createObservableStrategy();

        /*
         * Создаём копию исходной коллекции.
         *
         * Это важно: анимация не должна
         * изменять таблицу до её завершения.
         */
        CustomArrayList<Car> copy = new CustomArrayList<>();

        for (Car car : cars) {
            copy.add(car);
        }


        // Указываем визуализатору выбранное поле.
        visualizer.setValueExtractor(valueExtractor);

        // Создаём кадры с полными данными автомобилей.
        animationFrames = visualizationService.createFrames(
                copy,
                createComparator(),
                strategy
        );

        /*
         * Сбрасываем статистику.
         */
        comparisonLabel.setText("Сравнения: 0");
        swapLabel.setText("Изменения: 0");

        /*
         * Счётчики обновляются по мере
         * воспроизведения кадров.
         */
        final int[] comparisons = {0};
        final int[] updates = {0};

        /*
         * Запускаем анимацию.
         */

        animationPlayer.play(animationFrames, speedSlider.getValue(),

                // Обработчик каждого кадра.
                frame -> {

                    visualizer.showFrame(frame);

                    SortEvent event = frame.event();

                    if (event == null) {
                        return;
                    }

                    if (event.type() == SortEvent.Type.COMPARE) {
                        comparisons[0]++;
                    } else {
                        updates[0]++;
                    }

                    comparisonLabel.setText("Сравнения: " + comparisons[0]);

                    swapLabel.setText("Изменения: " + updates[0]);
                },

                // Вызывается только после завершения анимации.
                () -> {

                    // copy уже отсортирован алгоритмом.
                    cars = copy;

                    // Обновляем таблицу автомобилей.
                    refreshTable();

                    setStatus("Сортировка завершена: " + algorithmComboBox.getValue() + " по полю " + fieldComboBox.getValue());
                }
        );

        setStatus("Анимация сортировки запущена.");
    }

    /**
     * Пауза.
     */
    @FXML
    private void handlePauseAnimation() {

        animationPlayer.pause();

        setStatus("Анимация приостановлена.");
    }

    /**
     * Продолжение.
     */
    @FXML
    private void handleResumeAnimation() {

        animationPlayer.resume();

        setStatus("Анимация продолжена.");
    }

    /**
     * Сброс.
     */
    @FXML
    private void handleResetAnimation() {

        animationPlayer.stop();

        if (!animationFrames.isEmpty()) {
            visualizer.showFrame(animationFrames.get(0));
        }

        comparisonLabel.setText("Сравнения: 0");
        swapLabel.setText("Изменения: 0");

        setStatus("Анимация сброшена.");
    }

    /**
     * Создание наблюдаемого алгоритма.
     */
    private ObservableSortStrategy<Car>
    createObservableStrategy() {

        String selected = algorithmComboBox.getValue();

        return switch (selected) {

            case "Insertion Sort" ->
                    new ObservableInsertionSort<>();

            case "Selection Sort" ->
                    new ObservableSelectionSort<>();

            default ->
                    new ObservableBubbleSort<>();
        };
    }

}
