package ru.agapovla.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.agapovla.entity.Photo;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, UUID> {

    @Query("SELECT p FROM Photo p WHERE p.carOrPartId = :carOrPartId")
    List<Photo> findByCarOrPartId(UUID carOrPartId);
}
