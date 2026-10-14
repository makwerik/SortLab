
package ru.sortlab.io;

import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.model.Car;
import ru.sortlab.validation.CarValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Чтение автомобилей
 * из текстовых файлов CSV/TXT.
 *
 * Поддерживаем формат:
 *
 * model;power;year
 * BMW M5;600;2020
 * Audi RS6;630;2023
 *
 * Все строки должны содержать
 * ровно три значения.
 */
public final class CarFileReader {

    private CarFileReader() {
    }

    /**
     * Читает файл и возвращает коллекцию Car.
     *
     * @param path путь к файлу
     * @return коллекция автомобилей
     * @throws IOException если файл невозможно прочитать
     */
    public static CustomArrayList<Car> read(Path path)
            throws IOException {

        //Создаём коллекцию для результатов.

        CustomArrayList<Car> cars = new CustomArrayList<>();


        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

            String line;

            // Номер текущей строки файла.
            int lineNumber = 0;

            //readLine() возвращает следующую строку.
            while ((line = reader.readLine()) != null) {

                lineNumber++;


                line = line.trim();

                //Пропускаем пустые строки.
                if (line.isEmpty()) {
                    continue;
                }

                //Пропускаем заголовок CSV, если он находится в первой строке.

                if (lineNumber == 1 && line.equalsIgnoreCase("model;power;year")) {
                    continue;
                }

                //Разделяем строку по символу и сохраняю пустые значения в конце строки

                String[] parts = line.split(";", -1);

                // Проверяю количество столбцов
                if (parts.length != 3) {

                    throw new IllegalArgumentException(
                            "Строка " + lineNumber +
                                    ": ожидается 3 поля, получено " +
                                    parts.length
                    );
                }

                try {
                    Car car = CarValidator.parse(
                            parts[0],
                            parts[1],
                            parts[2]
                    );


                    cars.add(car);

                } catch (IllegalArgumentException exception) {

                    // добавляю номер строки к сообщению об ошибке.

                    throw new IllegalArgumentException(
                            "Ошибка в строке " + lineNumber + ": " + exception.getMessage(), exception);
                }
            }
        }

        return cars;
    }
}
