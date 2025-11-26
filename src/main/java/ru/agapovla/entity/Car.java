package ru.agapovla.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    private String mark;
    private String model;
    private String generation;
    @Column(name = "car_year")
    private Integer year;

    private String gosNumber;
    private String vinOrFrame;

    private String engineModel;
    private String transmissionType;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Part> parts = new ArrayList<>();

}
