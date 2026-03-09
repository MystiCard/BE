package com.example.mysterycard.schedule;

import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.entity.Shipment;
import com.example.mysterycard.entity.Tracking;
import com.example.mysterycard.enums.OrderItemStatus;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.repository.OrderItemsRepo;
import com.example.mysterycard.repository.OrderRepo;
import com.example.mysterycard.repository.ShipmentRepo;
import com.example.mysterycard.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OrderSchedule {
    private final ShipmentRepo shipmentRepo;
    private final OrderService orderService;
    @Scheduled(cron = "0 1 0 * * ?")
    @Transactional
    public void confirmRecieved()
    {
        List<Shipment> shipments = shipmentRepo.findByShipmentStatus(ShippingStatus.DELIVERED);
        for(Shipment s : shipments)
        {
            Tracking tracking = s.getTrackingList().getLast();
            if(tracking.getCreateAt().plusDays(3).isBefore(LocalDateTime.now()))
            {
                Set<OrderItem> orderItems = s.getOrderItems();
                for(OrderItem item : orderItems)
                {

                    if(item.getOrderItemStatus().equals(OrderItemStatus.RETURNING) || item.getOrderItemStatus().equals(OrderItemStatus.CONFIRMED))
                    {
                       orderService.confirmReceiveCard(item.getOrderItemId());
                    }
                }
            }
        }
    }
}
