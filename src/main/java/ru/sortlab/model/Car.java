package ru.sortlab.model;


/**
 * Класс описывающий автомобиль.
 * Каждый авто имеет: модель, мощность, год выпуска.
 * Класс финальный, поэтому от этого класса нельзя будет унаследоваться
 */
public final class Car {

    private final String model;
    private final int power;
    private final int year;

    /**
     * Приватный конструктор, потому что все автомобили будут создаваться через билдер.
     */
    private Car(Builder builder){

        // копируем модель, мощность, год из билдера
        this.model = builder.model;
        this.power = builder.power;
        this.year = builder.year;

    }

    public String getModel(){
        return model;
    }

    public int getPower() {
        return power;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString(){
        return "Car{" + "model=" + model +", power=" + power + ", year=" + year + "}";
    }

    /**
     * Вложенный строительный класс Builder
     */
    public static class Builder{

        // Временные характеристики будущего автомобиля
        private String model;
        private int power;
        private int year;


        /**
         * Методы для установки модели, мощности и года,
         * возвращают Builder, чтобы можно было вызывать методы по цепочке
         */
        public Builder model(String model){
            this.model = model;
            return this;
        }

        public Builder power(int power){
            this.power = power;
            return this;
        }

        public Builder year(int year){
            this.year = year;
            return this;
        }

        /**
         * Метод завершающий строение автомобиля
         */
        public Car build(){

            // Валидация данных
            if (model == null || model.isBlank()){
                throw new IllegalArgumentException("Модель автомобиля не может быть пустой");
            }

            if (power <= 0){
                throw  new IllegalArgumentException("Мощность автомобиля не иожет быть меньше или равна нулю");
            }

            if (year < 1886 || year > 2100){
                throw new IllegalArgumentException("Некорректный год производства");
            }

            return new Car(this);
        }
    }
}
