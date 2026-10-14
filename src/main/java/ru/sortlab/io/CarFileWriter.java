
package ru.sortlab.io;

import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.model.Car;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Класс для записи автомобилей в файл
 */
public final class CarFileWriter {

    private CarFileWriter(){};

    /**
     * Дописывает автомобили в текстовый файл.
     *
     * @param path путь к выходному файлу
     * @param cars коллекция автомобилей
     * @param description описание результата
     */
    public static void append(Path path, CustomArrayList<Car> cars, String description) throws IOException{

        // проверка наличия родительской папки
        Path parent = path.getParent();

        if (parent != null){
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)){

            writer.write("____ " + description + "____" );
            writer.newLine();

            writer.write("model;power;year");
            writer.newLine();

            for (Car car: cars){

                // формирую строку для записи
                String line = car.getModel() + ";" + car.getPower() + ";" + car.getYear();

                writer.write(line);
                writer.newLine();
            }

            // пустая строка, чтобы разделять результаты
            writer.newLine();
        }
    }
}
