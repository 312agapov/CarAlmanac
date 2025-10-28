package ru.agapovla.controller;

import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.agapovla.manager.WindowManager;

@Controller
public class MainController {

    private final WindowManager windowManager;


    @Autowired
    public MainController(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    @FXML
    public void chooseCar() {
        windowManager.openWindow("/view/choose-car.fxml", "Выбор авто", -1, -1);
    }

    @FXML
    public void changeCarList() {
        windowManager.openWindow("/view/change-car-list.fxml", "Редактирование списка авто", -1, -1);
    }
}
