package ru.sortlab.generator;
import ru.sortlab.collection.CustomArrayList;
import ru.sortlab.model.Car;

import java.util.Random;
import java.util.stream.Stream;

/**
 * Класс для генерации случайных автомобилей
 */
public class CarGenerator {

    private static final String[] MODELS = {
            "BMW M5",
            "Audi RS6",
            "Toyota Supra",
            "Mercedes C63",
            "Porsche 911",
            "Ford Mustang",
            "Nissan GT-R",
            "Lexus IS",
            "Honda Civic",
            "Volkswagen Golf"
    };

    private final Random random;

    public CarGenerator(){
        // создаём собственный рандом
        this(new Random());
    }

    /**
     * Второй конструктор.
     *
     * Позволяет передать Random извне.
     *
     * Это полезно для тестов, поскольку
     * можно использовать фиксированный seed.
     */
    public CarGenerator(Random random){
        if (random == null){
            throw new IllegalArgumentException("Random не может быть null");
        }

        this.random = random;
    }


    /**
     * Генерация одного случайного авто
     * @return готовый объект Car
     */
    public Car generateOne(){

        // выбираем случайный индекс автомобиля
        int modelIndex = random.nextInt(MODELS.length);

        String model = MODELS[modelIndex];

        // генерируем мощность авто от 0 до 800
        int power = 100 + random.nextInt(701);

        // Генерируем год от 1990 до 2026
        int year = 1990 + random.nextInt(37);

        return new Car.Builder().model(model).power(power).year(year).build();
    }

    /**
     * Генерация коллекции авто
     * @param count кол-во авто для генерации
     * @return Коллекцию автомобилей
     */
    public CustomArrayList<Car> generate(int count){

        if (count < 0){
            throw new IllegalArgumentException("Кол-во автомобилей не может быть отрицательным");
        }

        CustomArrayList<Car> cars = new CustomArrayList<>();

        /*
         * Stream.generate создаёт поток
         * случайных автомобилей.
         *
         * this::generateOne — ссылка на метод
         * текущего объекта.
         *
         * limit(count) ограничивает количество.
         *
         * forEach(cars::add) добавляет
         * автомобили в нашу коллекцию.
         *
         * Используем последовательный Stream,
         * поскольку CustomArrayList не является
         * потокобезопасной коллекцией.
         */

        Stream.generate(this::generateOne).limit(count).forEach(cars::add);

        return cars;
    }
}
