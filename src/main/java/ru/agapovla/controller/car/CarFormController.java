package ru.agapovla.controller.car;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import ru.agapovla.entity.Car;
import ru.agapovla.entity.Photo;
import ru.agapovla.manager.WindowManager;
import ru.agapovla.service.CarService;
import ru.agapovla.service.PhotoService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CarFormController {

    @Value("${app.upload-dir}")
    private String uploadDir;

    private final WindowManager windowManager;

    private final CarService carService;
    private final PhotoService photoService;

    @FXML private TextField markField,
            modelField,
            generationField,
            yearField,
            gosNumberField,
            vinOrFrameField,
            engineModelField,
            transmissionTypeField;

    @Setter
    private Runnable onSaved;

    private Car editingCar;

    private final List<File> selectedPhotos = new ArrayList<>();
    private final List<Photo> existingPhotos = new ArrayList<>();

    @FXML
    private FlowPane photoPreviewPane;

    // заполняет формы при edit авто + добавляет фото из бд в существующие фото +
    // выводит в fxml фото, что уже есть и те, которые добавили только что
    public void setCar(Car car){

        if (car == null){
            return;
        }

        this.editingCar = car;

        markField.setText(car.getMark());
        modelField.setText(car.getModel());
        generationField.setText(car.getGeneration());
        if (car.getYear() != null) {
            yearField.setText(car.getYear().toString());
        } else {
            yearField.setText("");
        }
        gosNumberField.setText(car.getGosNumber());
        vinOrFrameField.setText(car.getVinOrFrame());
        engineModelField.setText(car.getEngineModel());
        transmissionTypeField.setText(car.getTransmissionType());

        existingPhotos.clear();
        existingPhotos.addAll(photoService.getPhotosByCarOrPartUUID(car.getId()));
        showPhotoPreviews();
    }

    //при нажатии на кнопку "Сохранить!"
    @FXML
    public void onSaveCar(){
        boolean isNew = editingCar == null;

        if (!isNew) {
            editingCar.setMark(markField.getText().trim());
            editingCar.setModel(modelField.getText().trim());
            editingCar.setGeneration(generationField.getText().trim());
            editingCar.setYear(Integer.parseInt(yearField.getText()));
            editingCar.setGosNumber(gosNumberField.getText().trim());
            editingCar.setVinOrFrame(vinOrFrameField.getText().trim());
            editingCar.setEngineModel(engineModelField.getText().trim());
            editingCar.setTransmissionType(transmissionTypeField.getText().trim());

            carService.editCar(editingCar);
        } else {
            editingCar = Car.builder()
                    .mark(markField.getText())
                    .model(modelField.getText())
                    .generation(generationField.getText())
                    .year(Integer.parseInt(yearField.getText()))
                    .gosNumber(gosNumberField.getText())
                    .vinOrFrame(vinOrFrameField.getText())
                    .engineModel(engineModelField.getText())
                    .transmissionType(transmissionTypeField.getText())
                    .build();
            editingCar = carService.addCar(editingCar);
        }

        if (!selectedPhotos.isEmpty()){
            List<String> savedPaths = saveFilesToDisk(selectedPhotos);
            photoService.addPhotos(editingCar.getId(), savedPaths);
            selectedPhotos.clear();
        }

        if (onSaved != null){
            onSaved.run();
        }

        Stage stage = (Stage) markField.getScene().getWindow();
        stage.close();
    }

    // при нажатии на кнопку "Выбрать фото"
    @FXML
    public void onAddPhotos(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите фотографии");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(markField.getScene().getWindow());
        if (files != null){
            selectedPhotos.addAll(files);
            showPhotoPreviews();
        }
    }

    // рисует в окне квадратики с фотографиями, которые уже есть и которые были только что добавлены
    private void showPhotoPreviews(){
        photoPreviewPane.getChildren().clear();

        for (Photo photo : existingPhotos){
            File file = new File(photo.getFilePath());
            if (file.exists()) {
                ImageView imageView = new ImageView(new Image(file.toURI().toString()));
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);

                Button deleteBtn = new Button("✖");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-weight: bold;");

                VBox photoBox = new VBox(imageView, deleteBtn);
                photoBox.setAlignment(Pos.CENTER);
                photoBox.setSpacing(3);

                deleteBtn.setOnAction(e -> {
                    File fileToDelete = new File(photo.getFilePath());
                    if (fileToDelete.exists()) {
                        fileToDelete.delete();
                    }
                    photoService.deletePhotoById(photo.getId());
                    existingPhotos.remove(photo);
                    showPhotoPreviews();
                });

                photoPreviewPane.getChildren().add(photoBox);
            }
        }

        for (File file : selectedPhotos){
            ImageView imageView = new ImageView(new Image(file.toURI().toString()));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            Button deleteBtn = new Button("✖");
            deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-weight: bold;");

            VBox photoBox = new VBox(imageView, deleteBtn);
            photoBox.setAlignment(Pos.CENTER);
            photoBox.setSpacing(3);

            deleteBtn.setOnAction(e -> {
                selectedPhotos.remove(file);
                showPhotoPreviews();
            });

            photoPreviewPane.getChildren().add(photoBox);
        }
    }

    // сохраняет файлы по указанному пути
    private List<String> saveFilesToDisk(List<File> files){
        List<String> paths = new ArrayList<>();
        File directory = new File(uploadDir + "/cars/" +
                editingCar.getMark() + "_" +
                editingCar.getModel() + "_" +
                editingCar.getVinOrFrame() + "/!carPhotos/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        for (File file : files) {
            String newFileName = UUID.randomUUID() + "_" + file.getName();
            File dest = new File(directory, newFileName);
            try {
                Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                paths.add(dest.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return paths;
    }
}
