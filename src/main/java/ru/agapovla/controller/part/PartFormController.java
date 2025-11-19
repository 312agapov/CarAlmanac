package ru.agapovla.controller.part;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import ru.agapovla.entity.Car;
import ru.agapovla.entity.Part;
import ru.agapovla.entity.Photo;
import ru.agapovla.manager.WindowManager;
import ru.agapovla.service.CarService;
import ru.agapovla.service.PartService;
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
public class PartFormController {

    @Value("${app.upload-dir}")
    private String uploadDir;

    private final WindowManager windowManager;

    @Getter
    @Setter
    private UUID currentCarUUID;

    private final CarService carService;
    private final PartService partService;
    private final PhotoService photoService;

    @FXML private TextField partNumberField,
            manufacturerField,
            shortInfoField,
            unitPriceField,
            amountField;

    @FXML private TextArea fullInfoField;

    @FXML private CheckBox doesFitCheckBox = new CheckBox("Подходит");
    @FXML private CheckBox isInstalledCheckBox = new CheckBox("Установлен");


    @Setter
    private Runnable onSaved;

    private Part editingPart;

    private final List<File> selectedPhotos = new ArrayList<>();
    private final List<Photo> existingPhotos = new ArrayList<>();

    @FXML
    private FlowPane photoPreviewPane;

    public void setPart(Part part) {
        if (part == null){
            return;
        }
        this.editingPart = part;

        partNumberField.setText(part.getPartNumber());
        manufacturerField.setText(part.getManufacturer());
        shortInfoField.setText(part.getShortInfo());
        fullInfoField.setText(part.getFullInfo());

        if (part.getUnitPrice() != null){
            unitPriceField.setText(part.getUnitPrice().toString());
        } else {
            unitPriceField.setText(" ");
        }

        if (part.getAmount() != null){
            amountField.setText(part.getAmount().toString());
        } else {
            amountField.setText("1");
        }
        doesFitCheckBox.setSelected(Boolean.TRUE.equals(part.getDoesFit()));
        isInstalledCheckBox.setSelected(Boolean.TRUE.equals(part.getIsInstalled()));

        existingPhotos.clear();
        existingPhotos.addAll(photoService.getPhotosByCarOrPartUUID(part.getId()));
        showPhotoPreviews();

        javafx.application.Platform.runLater(() -> partNumberField.getParent().requestFocus());
    }

    @FXML
    public void onSavePart(){

        if (editingPart != null){
            editingPart.setPartNumber(partNumberField.getText().trim());
            editingPart.setManufacturer(manufacturerField.getText().trim());
            editingPart.setShortInfo(shortInfoField.getText().trim());
            editingPart.setFullInfo(fullInfoField.getText().trim());
            editingPart.setUnitPrice(Integer.parseInt(unitPriceField.getText().trim()));
            editingPart.setAmount(Integer.parseInt(amountField.getText().trim()));
            editingPart.setDoesFit(doesFitCheckBox.isSelected());
            editingPart.setIsInstalled(isInstalledCheckBox.isSelected());

            partService.editPart(editingPart);
        } else {

            Car car = new Car();
            car.setId(currentCarUUID);

            editingPart = Part.builder()
                    .partNumber(partNumberField.getText())
                    .manufacturer(manufacturerField.getText())
                    .shortInfo(shortInfoField.getText())
                    .fullInfo(fullInfoField.getText())
                    .unitPrice(Integer.parseInt(unitPriceField.getText()))
                    .amount(Integer.parseInt(amountField.getText()))
                    .doesFit(doesFitCheckBox.isSelected())
                    .isInstalled(isInstalledCheckBox.isSelected())
                    .car(car)
                    .build();

            partService.addPart(editingPart);
        }

        if (!selectedPhotos.isEmpty()){
            List<String> savedPaths = saveFilesToDisk(selectedPhotos);
            photoService.addPhotos(editingPart.getId(), savedPaths);
            selectedPhotos.clear();
        }

        if (onSaved != null) {
            onSaved.run();
        }

        Stage stage = (Stage) partNumberField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onAddPhotos(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите фотографии");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(partNumberField.getScene().getWindow());
        if (files != null){
            selectedPhotos.addAll(files);
            showPhotoPreviews();
        }
    }

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

    private List<String> saveFilesToDisk(List<File> files){
        List<String> paths = new ArrayList<>();
        Car currentCar = carService.getCarById(currentCarUUID);
        File directory = new File(uploadDir + "/cars/" +
                currentCar.getMark() + "_" +
                currentCar.getModel() + "_" +
                currentCar.getVinOrFrame() + "/" + editingPart.getPartNumber() + "_" + editingPart.getShortInfo() + "/");
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
