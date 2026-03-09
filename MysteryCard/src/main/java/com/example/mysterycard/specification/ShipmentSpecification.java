package com.example.mysterycard.specification;

import com.example.mysterycard.entity.Order;
import com.example.mysterycard.entity.Shipment;
import com.example.mysterycard.enums.ShippingStatus;
import org.springframework.data.jpa.domain.Specification;

public class ShipmentSpecification {
    public static Specification<Shipment> findByStatus (ShippingStatus shippingStatus)
    {
        return (root, query, cb) ->

                shippingStatus != null ? cb.equal(root.get("shipmentStatus"),shippingStatus) : null;
    }
    public static Specification<Shipment> findByOrder (Order order)
    {
        return (root, query, cb) ->

                order != null ? cb.equal(root.get("order"),order) : null;
    }
}
