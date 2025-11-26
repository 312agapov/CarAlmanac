package ru.agapovla.manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
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

            setAppIcon(stage);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> void replaceWindow(String fxml, String title, Node anyNodeInCurrentWindow, Consumer<T> controllerInit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setControllerFactory(context::getBean);
            Parent newContent = loader.load();

            T controller = loader.getController();
            if (controllerInit != null){
                controllerInit.accept(controller);
            }

            Scene scene = anyNodeInCurrentWindow.getScene();
            scene.setRoot(newContent);

            Stage stage = (Stage) scene.getWindow();
            if (title != null) {
                stage.setTitle(title);
            }

            setAppIcon(stage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setAppIcon(Stage stage) {
        try {
            InputStream iconStream = getClass().getResourceAsStream("/icon.jpg");
            if (iconStream != null) {
                Image icon = new Image(iconStream, 64, 64, true, true);
                stage.getIcons().add(icon);
            } else {
                System.err.println("Иконка icon.jpg не найдена в resources");
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки иконки: " + e.getMessage());
        }
    }
}

