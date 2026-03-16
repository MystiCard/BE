package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "FeedBack")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID feedBackId;
    private String comment;
    private int rating;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name="buyer_id")
    private Users buyer;
    @OneToOne
    @JoinColumn(name = "orderDetai_id")
    private OrderItem orderItem;
    @ManyToOne
    @JoinColumn(name = "seller")
    public Users seller;
    @OneToMany(mappedBy = "feedback",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    public List<Image> images = new ArrayList<>();
}
