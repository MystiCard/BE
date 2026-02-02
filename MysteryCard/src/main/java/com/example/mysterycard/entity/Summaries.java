package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "Sumaries")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Summaries {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sumariesId;
    private LocalDate localDate;
    private long totalAmount;
    private long totalPayment;
    private long success;
    private long error;
}
