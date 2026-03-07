package com.example.mysterycard.specification;

import com.example.mysterycard.entity.Order;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
    public static  Specification<Order> findByStatus(OrderStatus orderStatus) {
        return (root, query, cb) -> orderStatus != null ? cb.equal(root.get("status"),orderStatus) : null;
    };
    public static  Specification<Order> findByBuyer(Users users) {
        return (root, query, cb) -> users != null ? cb.equal(root.get("buyer"),users) : null;
    };

}
