
package ru.sortlab.io;

import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.model.Car;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Ручной тест чтения и записи файлов.
 *
 * Создаём временный файл,
 * читаем его и проверяем автомобили.
 *
 * Затем проверяем запись результатов.
 */
public class FileServiceTest {

    public static void main(String[] args) throws Exception {

        /*
         * Создаём временную папку.
         *
         * Она нужна, чтобы тест
         * не изменял реальные файлы проекта.
         */
        Path tempDirectory =
                Files.createTempDirectory("sortlab-test-");

        Path input = tempDirectory.resolve("cars.csv");
        Path output = tempDirectory.resolve("results.txt");

        /*
         * Создаём тестовый CSV.
         */
        Files.writeString(
                input,
                "model;power;year\n" +
                        "BMW M5;600;2020\n" +
                        "Audi RS6;630;2023\n",
                StandardCharsets.UTF_8
        );

        /*
         * Загружаем автомобили.
         */
        CustomArrayList<Car> cars =
                CarFileReader.read(input);

        /*
         * Проверяем количество.
         */
        if (cars.getSize() != 2) {
            throw new AssertionError(
                    "Ожидалось 2 автомобиля"
            );
        }

        /*
         * Проверяем первый автомобиль.
         */
        if (!cars.get(0).getModel().equals("BMW M5")) {
            throw new AssertionError(
                    "Неверная модель первого автомобиля"
            );
        }

        /*
         * Записываем результат дважды.
         *
         * Это позволит проверить,
         * что данные дописываются,
         * а не перезаписываются.
         */
        CarFileWriter.append(output, cars, "Первый запуск");
        CarFileWriter.append(output, cars, "Второй запуск");

        /*
         * Читаем итоговый текст.
         */
        String result =
                Files.readString(output, StandardCharsets.UTF_8);

        /*
         * Проверяем, что присутствуют
         * оба заголовка результатов.
         */
        if (!result.contains("Первый запуск") ||
                !result.contains("Второй запуск")) {

            throw new AssertionError(
                    "Результаты не были дописаны"
            );
        }

        System.out.println(
                "Все тесты файлового ввода-вывода пройдены!"
        );
    }
}
