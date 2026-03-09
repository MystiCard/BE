package com.example.mysterycard.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.ShippingStatus;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ShipmentRepo extends JpaRepository<Shipment, UUID> {

    Page<Shipment>  findAllByShipmentStatusNotInAndShipper(Collection<ShippingStatus> shipmentStatuses, Users shipper, Pageable pageable);
@Query("""
select s from Shipment s where s.shipper is null or s.shipmentStatus = :status

""")
    Page<Shipment> findAllByNotHaveShipper(ShippingStatus status, Pageable pageable);
    List<Shipment> findByShipmentStatusIn(Collection<ShippingStatus> shipmentStatuses);
    List<Shipment> findByOrderItems(Set<OrderItem> orderItems);
    List<Shipment> findByBlindBoxResults(Set<BlindBoxResult> blindBoxResults);


    List<Shipment> findByShipmentStatus(ShippingStatus shipmentStatus);

    boolean existsByShipmentStatusIsInAndShipper(Collection<ShippingStatus> shipmentStatuses, Users shipper);
}
