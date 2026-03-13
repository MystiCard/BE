package com.example.mysterycard.specification;

import com.example.mysterycard.entity.Order;
import com.example.mysterycard.entity.Shipment;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.ShippingStatus;
import org.apache.catalina.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

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
    public static Specification<Shipment> findByStatus (List<ShippingStatus> statuses)
    {
        return (root, query, cb) ->

                statuses != null ? root.get("shipmentStatus").in(statuses) : null;
    }
    public static Specification<Shipment> findByUser (Users users)
    {
        return (root, query, cb) ->

                users != null ? cb.equal(root.get("shipper"),users) : null;
    }
}
