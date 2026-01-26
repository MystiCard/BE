package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "BlindBox_Card")
public class BlindBoxCard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID blindBoxCardId;

    @ManyToOne
    @JoinColumn(name = "blind_box_id")
    private BlindBox blindBox;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    private boolean status = true; // ví dụ trạng thái hoạt động
    private double rate; // ví dụ tỉ lệ rút
}

