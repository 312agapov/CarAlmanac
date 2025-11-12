package ru.agapovla.controller.part;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import ru.agapovla.entity.Car;
import ru.agapovla.entity.Part;
import ru.agapovla.manager.WindowManager;
import ru.agapovla.service.PartService;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PartFormController {

    private final WindowManager windowManager;

    @Getter
    @Setter
    private UUID currentCarUUID;

    private final PartService partService;

    @FXML private TextField partNumberField,
            manufacturerField,
            infoField,
            priceField;

    @FXML private CheckBox doesFitCheckBox = new CheckBox("Подходит");
    @FXML private CheckBox isInstalledCheckBox = new CheckBox("Установлен");


    @Setter
    private Runnable onSaved;

    private Part editingPart;

    public void setPart(Part part) {
        if (part == null){
            return;
        }
        this.editingPart = part;

        partNumberField.setText(part.getPartNumber());
        manufacturerField.setText(part.getManufacturer());
        infoField.setText(part.getInfo());
        if (part.getPrice() != null){
            priceField.setText(part.getPrice().toString());
        } else {
            priceField.setText(" ");
        }
        doesFitCheckBox.setSelected(Boolean.TRUE.equals(part.getDoesFit()));
        isInstalledCheckBox.setSelected(Boolean.TRUE.equals(part.getIsInstalled()));
    }

    @FXML
    public void onSavePart(){

        if (editingPart != null){
            editingPart.setPartNumber(partNumberField.getText().trim());
            editingPart.setManufacturer(manufacturerField.getText().trim());
            editingPart.setInfo(infoField.getText().trim());
            editingPart.setPrice(Integer.parseInt(priceField.getText().trim()));
            editingPart.setDoesFit(doesFitCheckBox.isSelected());
            editingPart.setIsInstalled(isInstalledCheckBox.isSelected());

            partService.editPart(editingPart);
        } else {

            Car car = new Car();
            car.setId(currentCarUUID);

            editingPart = Part.builder()
                    .partNumber(partNumberField.getText())
                    .manufacturer(manufacturerField.getText())
                    .info(infoField.getText())
                    .price(Integer.parseInt(priceField.getText()))
                    .doesFit(doesFitCheckBox.isSelected())
                    .isInstalled(isInstalledCheckBox.isSelected())
                    .car(car)
                    .build();

            partService.addPart(editingPart);
        }

        if (onSaved != null) {
            onSaved.run();
        }

        Stage stage = (Stage) partNumberField.getScene().getWindow();
        stage.close();
    }
}
