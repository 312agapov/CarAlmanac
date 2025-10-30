package ru.agapovla.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.agapovla.entity.Car;
import ru.agapovla.repository.CarRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    public Car addCar(Car car){
        if (car != null){
            return carRepository.save(car);
        } else {
            throw new IllegalArgumentException("ADD: На вход пришел пустой объект Car!");
        }
    }

    public Car getCarById(UUID carId){
        if (carId == null){
            throw new IllegalArgumentException("GET: На вход пришел пустой объект UUID!");
        }
        return carRepository.findById(carId).orElseThrow(() ->
                new NoSuchElementException("GET: Авто по такому UUID не найдено!"));
    }

    public List<Car> getAllCars(){
        return carRepository.findAll();
    }

    public Car editCar(Car car){
        return carRepository.save(car);
    }

    public Car deleteCarByID(UUID carId){
        Car deleteCar = carRepository.findById(carId).orElseThrow(() ->
                new NoSuchElementException("DELETE: Авто по такому UUID не найдено!"));
        carRepository.deleteById(carId);
        return deleteCar;
    }
}
