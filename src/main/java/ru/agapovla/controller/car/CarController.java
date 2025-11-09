package ru.agapovla.controller.car;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import ru.agapovla.controller.part.PartController;
import ru.agapovla.entity.Car;
import ru.agapovla.entity.Part;
import ru.agapovla.manager.WindowManager;
import ru.agapovla.service.CarService;
import ru.agapovla.service.PartService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CarController {

    private final WindowManager windowManager;

    private final CarService carService;
    private final PartService partService;

    @FXML
    private ListView<Car> carList;

    @FXML
    public void initialize(){
        List<Car> cars = carService.getAllCars();
        carList.setItems(FXCollections.observableArrayList(cars));

        carList.setCellFactory(carListView -> new ListCell<>() {
            @Override
            protected void updateItem(Car car, boolean empty) {
                super.updateItem(car, empty);
                if (empty || car == null) {
                    setText(null);
                } else {
                    setText(car.getMark() + " " +
                            car.getModel() + " (" +
                            car.getYear() + ") - " +
                            car.getGosNumber());
                }
            }
        });
    }

    @FXML
    public void onShowParts(){
        Car selectedCar = carList.getSelectionModel().getSelectedItem();
        List<Part> parts = partService.getPartsByCarId(selectedCar.getId());
        windowManager.replaceWindow("/view/parts/parts-view.fxml", "Запчасти для " +
                selectedCar.getMark() + " " +
                selectedCar.getModel() + " " +
                selectedCar.getYear(), carList, controller -> {
            if (controller instanceof PartController partController) {
                partController.setParts(parts);
                partController.setCurrentCarUUID(selectedCar.getId());
            }
        });
    }

    @FXML
    public void onAddCar(){
        windowManager.openWindow("/view/cars/car-form.fxml", "Добавить авто", 800, 600, controller -> {
            if (controller instanceof CarFormController carFormController){
                carFormController.setOnSaved(this::refreshList);
            }
        });
    }

    @FXML
    public void onEditCar(){
        Car selectedCar = carList.getSelectionModel().getSelectedItem();
        if (selectedCar == null) return;

        windowManager.openWindow("/view/cars/car-form.fxml", "Изменить авто", 800, 600, controller -> {
            if (controller instanceof CarFormController carFormController){
                carFormController.setCar(selectedCar);
                carFormController.setOnSaved(this::refreshList);
            }
        });
    }

    @FXML
    public void onDeleteCar(){
        Car selectedCar = carList.getSelectionModel().getSelectedItem();
        carService.deleteCarByID(selectedCar.getId());

        refreshList();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Удалено");
        alert.setHeaderText(null);
        alert.setContentText("Авто удалено успешно!");
        alert.showAndWait();
    }

    @FXML
    public void refreshList() {
        List<Car> cars = carService.getAllCars();
        carList.setItems(FXCollections.observableArrayList(cars));
    }
}
