package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "FeedBack")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID feedBackId;
    private String comment;
    private int rating;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name="buyer_id")
    private Users buyer;
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Users seller;
    @ManyToOne
    @JoinColumn(name = "blind_box_id")
    private BlindBox blindBox;

}
