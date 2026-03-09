package com.example.mysterycard.entity;

import com.example.mysterycard.enums.Rarity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CardRequired {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID cardRequiredId;
    private String cardName;
    private Rarity rate;
    private double basePrice;
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name ="category_id")
    private Category category;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;
    private String note;
    private RequiredStatus status = RequiredStatus.PENDING;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;
    public enum RequiredStatus{
        PENDING, APPROVED, REJECTED
    }


}
