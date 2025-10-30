package ru.agapovla.manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class WindowManager {

    private final ApplicationContext context;

    public void openWindow(String fxml, String title, int width, int height) {
        openWindow(fxml, title, width, height, null);
    }

    public <T> void openWindow(String fxml, String title, int width, int height, Consumer<T> controllerInit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            T controller = loader.getController();
            if (controllerInit != null) {
                controllerInit.accept(controller);
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
