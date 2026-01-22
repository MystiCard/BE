package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "CardCategory")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID categoryId;
    private String categoryName;
    @OneToMany(mappedBy = "category")
    private List<Card> cardlist = new ArrayList<>();
}
