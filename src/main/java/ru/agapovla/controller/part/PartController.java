package ru.agapovla.controller.part;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import ru.agapovla.entity.Part;
import ru.agapovla.manager.WindowManager;
import ru.agapovla.service.CarService;
import ru.agapovla.service.PartService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PartController {

    private final WindowManager windowManager;

    private final CarService carService;
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
                            (part.getPrice() != null ? part.getPrice() + "₽" : "Цена не указана"));
                }
            }
        });
    }

    @FXML
    public void onBack() {
        windowManager.replaceWindow("/view/cars/car-choose.fxml", null, partList, null);
    }
}
