package ru.agapovla.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String mark;
    private String model;
    private String generation;
    private Integer year;

    private String gosNumber;
    private String vinOrFrame;

    private String engineModel;
    private String transmissionType;

}
