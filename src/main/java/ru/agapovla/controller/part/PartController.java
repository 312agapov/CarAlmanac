package ru.agapovla.controller.part;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import ru.agapovla.controller.car.CarFormController;
import ru.agapovla.entity.Car;
import ru.agapovla.entity.Part;
import ru.agapovla.manager.WindowManager;
import ru.agapovla.service.CarService;
import ru.agapovla.service.PartService;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PartController {

    private final WindowManager windowManager;

    @Getter
    @Setter
    private UUID currentCarUUID;

    private final PartService partService;

    @FXML
    private ListView<Part> partList;

    @FXML
    public void setParts(List<Part> parts) {
        partList.setItems(FXCollections.observableArrayList(parts));

        partList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Part part, boolean empty) {
                super.updateItem(part, empty);
                if (empty || part == null) {
                    setText(null);
                } else {
                    setText(part.getPartNumber() + " — " +
                            part.getManufacturer() + " — " +
                            (part.getUnitPrice() != null ? part.getUnitPrice() + "₽" : "Цена не указана"));
                }
            }
        });
    }

    @FXML
    public void onAddPart(){
        windowManager.openWindow("/view/parts/part-form.fxml", "Добавить запчасть", 800, 650, controller -> {
            if (controller instanceof PartFormController partFormController){
                partFormController.setCurrentCarUUID(currentCarUUID);
                partFormController.setOnSaved(this::refreshList);
            }
        });
    }

    @FXML
    public void onEditPart(){
        Part selectedPart = partList.getSelectionModel().getSelectedItem();
        if (selectedPart == null) return;

        windowManager.openWindow("/view/parts/part-form.fxml", "Изменить запчасть", 800, 650, controller -> {
            if (controller instanceof PartFormController partFormController){
                partFormController.setPart(selectedPart);
                partFormController.setOnSaved(this::refreshList);
            }
        });
    }

    @FXML
    public void onDeletePart(){
        Part selectedPart = partList.getSelectionModel().getSelectedItem();
        partService.deletePartById(selectedPart.getId());

        refreshList();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Удалено");
        alert.setHeaderText(null);
        alert.setContentText("Запчасть удалена успешно!");
        alert.showAndWait();
    }

    @FXML
    public void refreshList() {
        List<Part> parts = partService.getPartsByCarId(currentCarUUID);
        partList.setItems(FXCollections.observableArrayList(parts));
    }

    @FXML
    public void onBack() {
        windowManager.replaceWindow("/view/cars/car-choose.fxml", null, partList, null);
    }
}
