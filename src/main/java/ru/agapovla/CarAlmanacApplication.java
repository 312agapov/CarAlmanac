package ru.agapovla;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

@SpringBootApplication
public class CarAlmanacApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = SpringApplication.run(CarAlmanacApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        loader.setControllerFactory(context::getBean);

        Parent root = loader.load();
        primaryStage.setTitle("Car Almanac");
        primaryStage.setScene(new Scene(root, -1, -1));

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.jpg")),
                64, 64, true, true);
        primaryStage.getIcons().add(icon);

        primaryStage.show();
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        System.out.println("Приложение запущено!");
        System.out.println(System.getProperty("file.encoding"));
        launch(args);
    }
}