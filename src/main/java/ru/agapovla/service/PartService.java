package ru.agapovla.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.agapovla.entity.Part;
import ru.agapovla.repository.PartRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartService {

    private final PartRepository partRepository;

    public Part addPart(Part part){
        if (part != null){
            return partRepository.save(part);
        } else {
            throw new IllegalArgumentException("ADD: На вход пришел пустой объект Part!");
        }
    }

    public Part getPartById(UUID partId){
        if (partId == null){
            throw new IllegalArgumentException("GET: На вход пришел пустой объект UUID!");
        }
        return partRepository.findById(partId).orElseThrow(() ->
                new NoSuchElementException("GET: Запчасти по такому UUID не найдено!"));
    }

    public List<Part> getPartsByCarId(UUID carId){
        return partRepository.findPartsByCarId(carId);
    }

    public void editPart(Part part){
        partRepository.save(part);
    }

    public void deletePartById(UUID partId){
        partRepository.findById(partId).orElseThrow(() ->
                new NoSuchElementException("DELETE: Запчасти по такому UUID не найдено!"));
        partRepository.deleteById(partId);
    }
}
