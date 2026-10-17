package ru.sortlab;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Точка входа в графическое приложение
 * Application - базовый класс JavaFX для создания главного окна приложения
 */
public class SortLabApplication extends Application {

    /**
     * Метод автоматически вызывается после запуска приложения
     * @param stage Окно приложения
     */
    @Override
    public void start(Stage stage) throws IOException{

        // загрузка интерфейса
        FXMLLoader loader = new FXMLLoader(SortLabApplication.class.getResource("/ru/sortlab/main-view.fxml"));

        // загружаем дерево компонентов, содержащего элементы интерфейса
        Parent root = loader.load();

        // содержимое окна с начальными параметрами
        Scene scene = new Scene(root, 1100, 720);

        // стили
        scene.getStylesheets().add(SortLabApplication.class.getResource("/ru/sortlab/styles.css").toExternalForm());

        // заголовок окна
        stage.setTitle("Sortlab - Java Sorting Visualizer");

        // установка сцена
        stage.setScene(scene);

        // минимальные размеры окна
        stage.setMinHeight(600);
        stage.setMinWidth(850);

        stage.show();
    }

    public static void main(String[] args) {
        // запуск javafx
        launch(args);
    }
}
