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
        File file = chooser.showOpenDialog(carTable.getScene().getWindow());

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
     */
    @FXML
    private void handleSort() {


        if (cars.isEmpty()) {

            showError("Сначала загрузите или создайте автомобили.");
            return;
        }

        // Получаем выбранный алгоритм.
        SortStrategy<Car> strategy = createStrategy();

        //Получаем выбранное правило сравнения.
        Comparator<Car> comparator = createComparator();


        SortingService<Car> service = new SortingService<>(strategy);

        //Запускаем наш собственный алгоритм.
        service.sort(cars, comparator);


        refreshTable();

        setStatus("Сортировка завершена: " + algorithmComboBox.getValue() + " по полю " + fieldComboBox.getValue());
    }

    /**
     * Сохранение результата.
     */
    @FXML
    private void handleSave() {

        if (cars.isEmpty()) {

            showError("Нет автомобилей для сохранения.");
            return;
        }

        FileChooser chooser = new FileChooser();

        chooser.setTitle("Сохранить результат");

        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовый файл","*.txt"));

        chooser.setInitialFileName("sortlab-results.txt");

        File file = chooser.showSaveDialog(carTable.getScene().getWindow());

        if (file == null) {
            return;
        }

        try {

            // дописываем результаты в файл.

            CarFileWriter.append(file.toPath(), cars,"Результат сортировки SortLab");

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
}
