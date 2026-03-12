package com.example.mysterycard.schedule;

import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.entity.Shipment;
import com.example.mysterycard.entity.Tracking;
import com.example.mysterycard.enums.OrderItemStatus;
import com.example.mysterycard.enums.OrderStatus;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.repository.OrderItemsRepo;
import com.example.mysterycard.repository.OrderRepo;
import com.example.mysterycard.repository.ShipmentRepo;
import com.example.mysterycard.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderSchedule {
    private final ShipmentRepo shipmentRepo;
    private final OrderService orderService;
    private  final  OrderItemsRepo orderItemsRepo;
    @Scheduled(cron = "0 00 00 * * ?")
    @Transactional
    public void confirmRecieved()
    {
        log.info(" Schedule order");
        List<Shipment> shipments = shipmentRepo.findByShipmentStatus(ShippingStatus.DELIVERED);
        for(Shipment s : shipments)
        {
            Tracking tracking = s.getTrackingList().getLast();
            log.info("Tracking {}", tracking.getCreateAt());
            if(tracking.getCreateAt().plusDays(3).isBefore(LocalDateTime.now()))
            {
                log.info(" Schedule order 1-2-3");

                orderService.confirmReceiveCard(s.getShipmentId());
            }
        }
    }
    @Scheduled(cron = "0 00 00 * * ?")
    @Transactional
    public void cancleOrderDetail()
    {
         List<OrderItem> orderItems = orderItemsRepo.findByOrderItemStatusAndOrder_StatusNot(OrderItemStatus.PENDING_CONFIRM,OrderStatus.PAID);
         for (OrderItem orderItem : orderItems)
         {
             if(orderItem.getOrder().getOrderDate().plusDays(2).isBefore(LocalDateTime.now()))
             {
                 orderService.cancleOrder(orderItem.getOrder().getOrderId());
             }
         }
    }
}
