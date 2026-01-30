package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Entity
@Table(name = "Image")
@NoArgsConstructor
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID imageId;
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name="card_id")
    private Card card;
}
