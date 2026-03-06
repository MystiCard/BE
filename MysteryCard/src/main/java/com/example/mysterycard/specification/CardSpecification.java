package com.example.mysterycard.specification;

import com.example.mysterycard.entity.Card;
import org.springframework.data.jpa.domain.Specification;

public class CardSpecification {
    public static Specification<Card> findByName(String name) {
        return (root, query, cb) -> name != null ? cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%") : null;
    }
}
