package ru.sortlab.validation;
import ru.sortlab.model.Car;

/**
 * Класс валидации текстовых данных
 */
public final class CarValidator {

    /*
     * Закрытый конструктор.
     *
     * Класс содержит только статический метод.
     * Создавать объект CarValidator не требуется.
     */
    private CarValidator(){};

    /**
     * Создаёт автомобиль из текстовых значений.
     *
     * @param modelText модель автомобиля
     * @param powerText мощность в виде строки
     * @param yearText год выпуска в виде строки
     *
     * @return готовый объект Car
     */
    public static Car parse(String modelText, String powerText, String yearText){

        if (modelText == null || powerText == null || yearText == null){
            throw new IllegalArgumentException("Поля автомобиля не могут быть null");
        }

        // удаляем пробелы
        String model = modelText.trim();

        // преобразуем мощность и год в числа
        int power;
        int year;

        try {
            power = Integer.parseInt(powerText.trim());
            year = Integer.parseInt(yearText.trim());
        } catch (NumberFormatException exception){
            throw new IllegalArgumentException("Мощность и год должны быть целыми числами", exception);
        }

        return new Car.Builder().model(model).power(power).year(year).build();
    }
}
