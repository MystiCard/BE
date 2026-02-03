package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Entity
@Table(name = "Image")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID imageId;
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name="card_id")
    private Card card;
    @ManyToOne
    @JoinColumn(name="tracking_id")
    private Tracking tracking;
}
