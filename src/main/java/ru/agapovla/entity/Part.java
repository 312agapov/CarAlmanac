package ru.agapovla.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "parts")
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String partNumber;
    private String manufacturer;
    private String info;
    private Integer price;
    private Boolean doesFit;
    private Boolean isInstalled;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

}
