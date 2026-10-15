package ru.sortlab.input;


import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.model.Car;
import ru.sortlab.validation.CarValidator;

import java.util.Scanner;

/**
 * Класс для ручного ввода автомобилей
 */
public class CarManualInput {

    private final Scanner scanner;

    public CarManualInput(Scanner scanner){
        if (scanner == null){
            throw new IllegalArgumentException("Scanner не может быть null");
        }

        this.scanner = scanner;
    }

    /**
     * Метод для ввода кол-ва автомобилей
     * @param count количество автомобилей
     * @return коллекция введённых объектов
     */
    public CustomArrayList<Car> readCars(int count){

        if (count < 0){
            throw new IllegalArgumentException("Количество автомобилей не может быть отрицательным");
        }

        CustomArrayList<Car> cars = new CustomArrayList<>();

        // повторяю ввод count раз
        for (int i = 0; i < count; i++){

            // для каждого авто запрашиваем корректные данные
            while (true){
                System.out.println("\nАвтомобиль номер: " + i);

                System.out.print("Введите модель автомобиля: ");
                String model = scanner.nextLine();

                System.out.print("Введите мощность автомобиля: ");
                String power = scanner.nextLine();

                System.out.print("Введите год выпуска автомобиля: ");
                String year = scanner.nextLine();

                // валидируем данные
                try{
                    Car car = CarValidator.parse(model, power, year);

                    cars.add(car);

                    // выходим из цикла while
                    break;
                }catch (IllegalArgumentException exception){
                    System.out.println("Ошибка " + exception.getMessage());
                    System.out.println("Попробуйте ввести автомобиль заново");
                }
            }
        }
        return cars;
    }
}
