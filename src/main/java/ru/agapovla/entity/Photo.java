package ru.agapovla.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    private String filePath;
    private UUID carOrPartId;
}
