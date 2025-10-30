package ru.agapovla.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.agapovla.entity.Part;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartRepository extends JpaRepository<Part, UUID> {

    List<Part> findPartsByCarId(UUID carId);
    
}
