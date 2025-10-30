package ru.agapovla.controller;

import javafx.fxml.FXML;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import ru.agapovla.manager.WindowManager;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final WindowManager windowManager;

    @FXML
    public void onChooseCar(){
        windowManager.openWindow("/view/cars/car-choose.fxml", "Выбор для авто для поиска запчастей", 800, 600);
    }

    @FXML
    public void onEditCars(){
        windowManager.openWindow("/view/cars/cars-edit.fxml", "Меню редактирования парка авто!", 800, 600);
    }
}
