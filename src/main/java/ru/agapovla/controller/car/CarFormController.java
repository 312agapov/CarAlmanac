package ru.agapovla.controller.car;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import ru.agapovla.entity.Car;
import ru.agapovla.manager.WindowManager;
import ru.agapovla.service.CarService;

@Controller
@RequiredArgsConstructor
public class CarFormController {

    private final WindowManager windowManager;

    private final CarService carService;

    @FXML private TextField markField;
    @FXML private TextField modelField;
    @FXML private TextField generationField;
    @FXML private TextField yearField;

    @FXML private TextField gosNumberField;
    @FXML private TextField vinOrFrameField;

    @FXML private TextField engineModelField;
    @FXML private TextField transmissionTypeField;

    @Setter
    private Runnable onSaved;

    private Car editingCar;

    public void setCar(Car car) {
        if (car == null){
            return;
        }
        this.editingCar = car;

        markField.setText(car.getMark());
        modelField.setText(car.getModel());
        generationField.setText(car.getGeneration());
        if (car.getYear() != null){
            yearField.setText(car.getYear().toString());
        } else {
            yearField.setText(" ");
        }
        gosNumberField.setText(car.getGosNumber());
        vinOrFrameField.setText(car.getVinOrFrame());
        engineModelField.setText(car.getEngineModel());
        transmissionTypeField.setText(car.getTransmissionType());
    }

    @FXML
    public void onSaveCar(){

        if (editingCar != null){
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

            carService.addCar(editingCar);
        }

        if (onSaved != null) {
            onSaved.run();
        }

        Stage stage = (Stage) markField.getScene().getWindow();
        stage.close();
    }
}
