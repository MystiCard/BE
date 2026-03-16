package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.*;
import com.example.mysterycard.dto.request.*;
import com.example.mysterycard.dto.response.*;
import com.example.mysterycard.dto.request.CalculateFeeRequest;
import com.example.mysterycard.dto.request.OrderCardRequest;
import com.example.mysterycard.dto.request.ShipmentRequest;
import com.example.mysterycard.dto.request.UpdateShipmentRequest;
import com.example.mysterycard.dto.response.OrderCardResponse;
import com.example.mysterycard.dto.response.OrderItemResponse;
import com.example.mysterycard.dto.response.OrderResponse;
import com.example.mysterycard.dto.response.PageResponse;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.OrderItemStatus;
import com.example.mysterycard.enums.OrderStatus;
import com.example.mysterycard.enums.ReturnRequestStatus;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.OrderItemMapper;
import com.example.mysterycard.mapper.OrderMapper;
import com.example.mysterycard.mapper.ShipmentMapper;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.OrderService;
import com.example.mysterycard.service.ShipmentService;
import com.example.mysterycard.service.TransactionService;
import com.example.mysterycard.specification.OrderSpecification;
import com.example.mysterycard.specification.ShipmentSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepo orderRepo;
    private final OrderItemsRepo orderItemsRepo;
    private final ShipmentService shipmentService;
    private final UsersRepo usersRepo;
    private final ListSellerRepo listSellerRepo;
    private final ShipmentRepo shipmentRepo;
    private final OrderItemMapper orderItemMapper;
    private final ShipmentMapper shipmentMapper;
    private final TransactionService transactionService;
    private final BlindBoxResultRepo blindBoxResultRepo;
    @Override
    @Transactional
    public OrderCardResponse createOrder(OrderCardRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(email);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Order order = Order.builder()
                .buyer(users)
                .build();
        order = orderRepo.save(order);
        order.setOrderItemList(new ArrayList<>());
        double totalAmount = 0;
        List<OrderItemResponse> allOrderItemsResponse = new ArrayList<>();
        if (request.getOrderItemsList() != null && !request.getOrderItemsList().isEmpty()) {

            Map<UUID, List<OrderCardRequest.OrderItems>> groupedBySeller = request.getOrderItemsList().stream()
                    .collect(Collectors.groupingBy(item -> {
                        ListSeller ls = listSellerRepo.findById(item.getListSellerId())
                                .orElseThrow(() -> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND));
                        return ls.getSeller().getUserId();
                    }));

            for (Map.Entry<UUID, List<OrderCardRequest.OrderItems>> orderItems : groupedBySeller.entrySet()) {
                List<OrderItem> orderItemList = new ArrayList<>();
                ListSeller listSeller = listSellerRepo.findById(orderItems.getValue().getFirst().getListSellerId())
                        .orElseThrow(() -> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND));
                double totalOrderItems = 0L;
                List<UUID> orderItemIds = new ArrayList<>();
                for (OrderCardRequest.OrderItems item : orderItems.getValue()) {
                    log.info("Item {}", item.toString());
                    ListSeller ls = listSellerRepo.findById(item.getListSellerId())
                            .orElseThrow(() -> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND));
                    if (item.getQuantity() > ls.getQuantity()) {
                        throw new AppException(ErrorCode.QUANTITY_OVER_AVAIABLE);
                    }

                    OrderItem orderIt = OrderItem.builder()
                            .quantity(item.getQuantity())
                            .listSeller(ls)
                            .order(order)
                            .price(ls.getPrice())
                            .build();
                    order.getOrderItemList().add(orderIt);
                    orderItemList.add(orderItemsRepo.save(orderIt));
                    orderItemIds.add(orderIt.getOrderItemId());
                    double price = orderIt.getQuantity() * ls.getPrice();
                    totalOrderItems += price;
                    totalAmount += price;
                }
                Long shipfee = null ;

                try{
                     shipfee = shipmentService.calculatFeeShip(
                            CalculateFeeRequest.builder()
                                    .totalAmount(totalOrderItems)
                                    .fromDistrictId(Long.valueOf(listSeller.getSeller().getDistrictId()))
                                    .toWardId(String.valueOf(request.getToWardId()))
                                    .toDistrictId(request.getToDistrictId())
                                    .build()
                    );
                }catch (Exception e){
                    throw  new AppException(ErrorCode.CANNOT_CALCULATE_SHIP_FEE);
                }

                log.info("Calculator ship  {}", CalculateFeeRequest.builder()
                        .totalAmount(totalOrderItems)
                        .fromDistrictId(Long.valueOf(listSeller.getSeller().getDistrictId()))
                        .toWardId(String.valueOf(request.getToWardId()))
                        .toDistrictId(request.getToDistrictId())
                        .build().toString());
                totalAmount += shipfee;
                OrderItemResponse orderItemResponse = OrderItemResponse.builder()
                        .orderDetailResponseList(orderItemList.stream().map(orderItemMapper::entityToResponse).toList())
                        .shipfee(shipfee)
                        .shipmentResponse(shipmentService.createsShipment(
                                ShipmentRequest.builder()
                                        .orderItemId(orderItemIds)
                                        .toAddress(request.getBuyerAddress())
                                        .toDistrictId(request.getToDistrictId())
                                        .toWardId(request.getToWardId())
                                        .toPhone(request.getBuyerPhone())
                                        .fromPhone(listSeller.getSeller().getPhone())
                                        .fromAddress(listSeller.getSeller().getAddress())
                                        .fromDistrictId(Long.valueOf(listSeller.getSeller().getDistrictId()))
                                        .fromName(listSeller.getSeller().getName())
                                        .toName(request.getToName())
                                        .shipmentFee(shipfee)
                                        .build()
                        ))
                        .build();
                allOrderItemsResponse.add(orderItemResponse);
            }
        }
        order.setTotalAmount(totalAmount);
        OrderCardResponse orderCardResponse = orderMapper.entityToResponse(orderRepo.save(order));
        orderCardResponse.setOrderItems(allOrderItemsResponse);
        return orderCardResponse;
    }
    public OrderBlindBoxResultResponse createBlindBoxOrder(OrderBlinkBoxResultRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(email);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Order order = Order.builder()
                .buyer(users)
                .build();
        order = orderRepo.save(order);
        List<BlindBoxResultResponse> blindBoxResultResponses = new ArrayList<>();
        double totalAmount = 0;
        if(request.getBlindBoxResultIds()!=null&&!request.getBlindBoxResultIds().isEmpty()){
            for(UUID blindBoxResultId: request.getBlindBoxResultIds()){
                BlindBoxResult blindBoxResult = blindBoxResultRepo.findByBlindBoxResultId(blindBoxResultId).orElseThrow(
                        () -> new AppException(ErrorCode.BLIND_BOX_RESULT_NOT_FOUND)
                );
                blindBoxResult.setOrder(order);
                blindBoxResultRepo.save(blindBoxResult);
                BlindBoxResultResponse blindBoxResultResponse = BlindBoxResultResponse.builder()
                        .blindBoxResultId(blindBoxResult.getBlindBoxResultId())
                        .openedAt(blindBoxResult.getOpenedAt())
                        .cardName(blindBoxResult.getCard().getName())
                        .cardImageUrl(blindBoxResult.getCard().getImages().isEmpty() ? null : blindBoxResult.getCard().getImages().get(0).getImageUrl())
                        .rarity(blindBoxResult.getCard().getRarity().toString())
                        .blindBoxName(blindBoxResult.getBlindBox().getName())
                        .build();
                blindBoxResultResponses.add(blindBoxResultResponse);
                order.getBlindBoxResults().add(blindBoxResult);
            }
        }
        Long shipfee = shipmentService.calculatFeeShip(
                CalculateFeeRequest.builder()
                        .totalAmount(0)
                        .fromDistrictId(1803L)
                        .toWardId(String.valueOf(request.getToWardId()))
                        .toDistrictId(request.getToDistrictId())
                        .build()
        );
        log.info("Calculator ship  {}",shipfee);

        totalAmount+=shipfee;
        shipmentService.createsShipment(
                ShipmentRequest.builder()
                        .blindBoxResultId(request.getBlindBoxResultIds())
                        .toAddress(request.getBuyerAddress())
                        .toDistrictId(request.getToDistrictId())
                        .toWardId(request.getToWardId())
                        .toPhone(request.getBuyerPhone())
                        .fromPhone(request.getBuyerPhone())
                        .fromAddress("Thôn Cốc Thôn, Xã Cam Thượng, Huyện Ba Vì, Hà Nội")
                        .fromDistrictId(3695L)
                        .shipmentFee(shipfee)
                        .build()
        );
        order.setTotalAmount(totalAmount);
        orderRepo.save(order);

        OrderBlindBoxResultResponse orderBlindBoxResultResponse = OrderBlindBoxResultResponse.builder()
                .orderId(order.getOrderId())
                .totalAmount(shipfee)
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .blindBoxResults(blindBoxResultResponses)
                .build();
        return orderBlindBoxResultResponse;
    }

    @Override
    public OrderCardResponse getByOrderId(UUID orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        List<Shipment> shipments =  order.getShipmentList();
        List<OrderItemResponse> list = new ArrayList<>();
        for (Shipment s : shipments)
        {
            OrderItemResponse orderItemResponse =  OrderItemResponse.builder()
                    .shipmentResponse(shipmentMapper.entityToResponse(s))
                    .shipfee(s.getShipmentFee())
                    .orderDetailResponseList(s.getOrderItems().stream().map(orderItemMapper::entityToResponse).toList())
                    .build();
            list.add(orderItemResponse);
        }
        OrderCardResponse orderCardResponse = new OrderCardResponse();
        orderCardResponse.setOrderItems(list);
        return orderCardResponse;
    }

    @Override
    public PageResponse<OrderItemResponse> getByStatusShipment(MyOrderDetailRequest request, int page, int size) {
        page = page - 1;
      Order order = orderRepo.findById(request.getOrderId()).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
      Specification<Shipment> specification = Specification.allOf(
              ShipmentSpecification.findByOrder(order),
              ShipmentSpecification.findByStatus(request.getShippingStatus())
      );
      Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
      List<Shipment> shipments = shipmentRepo.findAll(specification);
        List<OrderItemResponse> result = new ArrayList<>();
        shipments.forEach((s)->{
            if(s.getOrderItems() != null && s.getOrderItems().size() > 0){
                result.add(
                        OrderItemResponse.builder()
                                .shipmentResponse(shipmentMapper.entityToResponse(s))
                                .shipfee(s.getShipmentFee())
                                .orderDetailResponseList(s.getOrderItems().stream().map(orderItemMapper::entityToResponse).toList())
                                .build()
                );

            }

        });

        int totalPages = result.size() / size;
        int from = page * size;
        int to = Math.min(((page + 1) * size), result.size());
        List<OrderItemResponse> orderItems = new ArrayList<>();
        if(from < to) {
            orderItems = result.stream().toList().subList(from, to);
        }
        PageResponse<OrderItemResponse> pageResponse = PageResponse.<OrderItemResponse>builder()
                .content(orderItems)
                .totalElements(result.size())
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .last(to == result.size())
                .build();
        return pageResponse;
    }

    @Override
    @Transactional
    public OrderItemResponse confirmReceiveCard(UUID shipmentId) {
        Shipment shipment = shipmentRepo.findById(shipmentId).orElseThrow(
                () -> new AppException(ErrorCode.SHIPMENT_NOT_FOUND)
        );
        if(!shipment.getShipmentStatus().equals(ShippingStatus.DELIVERED))
        {
            throw new AppException(ErrorCode.CAN_NOT_CONFIRM_RECEIVE);
        }
        shipmentService.update(UpdateShipmentRequest.builder()
                .shippingStatus(ShippingStatus.RECEIVED)
                .shipmentId(shipmentId)
                .build(), null);
        for (OrderItem orderItem : new ArrayList<>(shipment.getOrderItems())) {
            if (orderItem.getOrderItemStatus().equals(OrderItemStatus.CONFIRMED)) {
                orderItem.setOrderItemStatus(OrderItemStatus.RECIEVED);
                transactionService.releasePrice(orderItem);
            }

        }
        Order order = shipment.getOrderItems().stream().toList().getFirst().getOrder();
        int countReceived = 0;
        int countCancelled = 0;
        for (Shipment s : order.getShipmentList()) {

            if(s.getShipmentStatus() != null)
            {
                if (s.getShipmentStatus().equals(ShippingStatus.RECEIVED)) {
                    countReceived++;
                }
                if (s.getShipmentStatus().equals(ShippingStatus.CANCELLED)) {
                    countCancelled++;
                }
            }

        }
        if (countReceived == (order.getShipmentList().size()- countCancelled)) {
            order.setStatus(OrderStatus.COMPLETED);
        } else if (countReceived > 0) {
            order.setStatus(OrderStatus.PARTIAL_COMPLETED);

        }
        orderRepo.save(order);
        return OrderItemResponse.builder()
                .shipmentResponse(shipmentMapper.entityToResponse(shipment))
                .shipfee(shipment.getShipmentFee())
                .orderDetailResponseList(shipment.getOrderItems().stream().map(orderItemMapper::entityToResponse).toList())
                .build();
    }

    @Override
    @Transactional
    public OrderItemResponse.OrderDetailResponse cancleOrderItem(UUID orderItemId) {
        OrderItem orderItem = orderItemsRepo.findById(orderItemId).orElseThrow(
                () -> new AppException(ErrorCode.ORDER_ITEMS_NOT_FOUND)
        );
        ShippingStatus shippingStatus = orderItem.getShipments().stream().toList().getLast().getShipmentStatus();
        if ((!shippingStatus.equals(ShippingStatus.PENDING_APPROVED) || !shippingStatus.equals(ShippingStatus.PENDING)) &&  orderItem.getOrderItemStatus().equals(OrderItemStatus.CANCELLED)) {
            throw new AppException(ErrorCode.CAN_NOT_CANCEL_ORDER_ITEM);
        }
        orderItem.setOrderItemStatus(OrderItemStatus.CANCELLED);
        orderItemsRepo.save(orderItem);
        Shipment shipment = orderItem.getShipments().stream().toList().getLast();
        int countOrderItem = 0;
        int confirm = 0;
        for (OrderItem item : shipment.getOrderItems()) {
            if (item.getOrderItemStatus().equals(OrderItemStatus.CANCELLED)) {
                countOrderItem++;
            }
            if(item.getOrderItemStatus().equals(OrderItemStatus.CONFIRMED)) {
                confirm++;
            }
        }
        if (countOrderItem == shipment.getOrderItems().size()) {
            shipmentService.update(UpdateShipmentRequest.builder()
                    .shippingStatus(ShippingStatus.CANCELLED)
                    .shipmentId(shipment.getShipmentId())
                    .build(), null);
        }else if(confirm == (shipment.getOrderItems().size() - countOrderItem)) {
            // truong hop huy 1 phan trong shipment
            if(shipment.getShipmentStatus() != null && shipment.getShipmentStatus().equals(ShippingStatus.PENDING_APPROVED)) {
                shipmentService.update(UpdateShipmentRequest.builder()
                        .shippingStatus(ShippingStatus.PENDING)
                        .shipmentId(shipment.getShipmentId())
                        .build(), null);
            }
        }
        // update quanity list seller
        ListSeller listSeller = orderItem.getListSeller();
        listSeller.setQuantity(listSeller.getQuantity() + orderItem.getQuantity());
        listSellerRepo.save(listSeller);
        // hoan tien
        transactionService.releasePrice(orderItem);
        Order order = orderItem.getOrder();
        int countCancelled = 0;
        for (OrderItem item : order.getOrderItemList()) {
            if (item.getOrderItemStatus().equals(OrderItemStatus.CANCELLED)) {
                countCancelled++;
            }
        }
        if (countCancelled == order.getOrderItemList().size()) {
            order.setStatus(OrderStatus.CANCELLED);
        } else if (countCancelled > 0) {
            order.setStatus(OrderStatus.PARTIAL_CANCELLED);
        }
        orderRepo.save(order);
        return orderItemMapper.entityToResponse(orderItemsRepo.save(orderItem));
    }
    @Override
    @Transactional
    public OrderCardResponse cancleOrder(UUID orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(
                () -> new AppException(ErrorCode.ORDER_NOT_FOUND)
        );
        List<OrderItem> orderItems = new ArrayList<>(order.getOrderItemList());
        for (OrderItem orderItem : orderItems) {
            if(!orderItem.getOrderItemStatus().equals(OrderItemStatus.CANCELLED)) {
                cancleOrderItem(orderItem.getOrderItemId());
            }
        }
        return orderMapper.entityToResponse(orderRepo.save(order));
    }

    @Override
    public PageResponse<OrderItemResponse> getMyReturnOrderItem(ShippingStatus shippingStatus, int page, int size) {
        page = page - 1;
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(name);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        List<OrderItemResponse> result = new ArrayList<>();
        List<Shipment> shipments = shipmentRepo.findByShipmentStatus(shippingStatus);
        shipments.forEach(shipment -> {
            List<OrderItem> orderItems = new ArrayList<>();
            shipment.getOrderItems().forEach(orderItem -> {
                if (orderItem.getListSeller().getSeller().getUserId().equals(users.getUserId())
                        && orderItem.getReturnRequest() != null && orderItem.getReturnRequest().getStatus().equals(ReturnRequestStatus.PAID)) {
                    orderItems.add(orderItem);
                }
            });
            result.add(
                    OrderItemResponse.builder()
                            .shipmentResponse(shipmentMapper.entityToResponse(shipment))
                            .shipfee(shipment.getShipmentFee())
                            .orderDetailResponseList(orderItems.stream().map(orderItemMapper::entityToResponse).toList())
                            .build()
            );
        });
        int totalPages = result.size() / size;
        int from = page * size;
        int to = Math.min(((page + 1) * size), result.size());
        List<OrderItemResponse> orderItems = new ArrayList<>();
        if(from < to) {
            orderItems = result.stream().toList().subList(from, to);
        }
        PageResponse<OrderItemResponse> pageResponse = PageResponse.<OrderItemResponse>builder()
                .content(orderItems)
                .totalElements(result.size())
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .last(to == result.size())
                .build();
        return pageResponse;
    }

    @Override
    public Page<OrderResponse> getMyOrders(OrderStatus orderStatus, int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(email);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Specification<Order> specification = Specification.allOf( OrderSpecification.findByStatus(orderStatus),OrderSpecification.findByBuyer(users));
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("orderDate").descending());
        return orderRepo.findAll(specification,pageRequest).map(orderMapper::toOrderResponse);
    }

    @Override
    public boolean canCanleOrder(UUID orderId) {
        Users users = getCurrentUser();
        Order order = orderRepo.findById(orderId).orElseThrow(
                ()-> new AppException(ErrorCode.ORDER_NOT_FOUND)
        );
        List<Shipment> shipments = order.getShipmentList();
        int count = 0;
        for(Shipment shipment : shipments){
            if(shipment != null && shipment.getShipmentStatus() != null  && (shipment.getShipmentStatus().equals(ShippingStatus.PENDING) || shipment.getShipmentStatus().equals(ShippingStatus.PENDING_APPROVED)))
            {
                count++;
            }
        }
        return order.getBuyer().equals(users) && count != 0 &&  count == shipments.size();
    }

    @Override
    public Page<OrderItemResponse.OrderDetailResponse> listPending(int page, int size) {
        Users users = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("order.orderDate").ascending());
        Page<OrderItem> orderItems = orderItemsRepo.findByListSeller_SellerAndOrderItemStatusAndOrder_StatusNot(users,OrderItemStatus.PENDING_CONFIRM,OrderStatus.CREATED,pageable);
        return orderItems.map(orderItemMapper::entityToResponse);
    }

    @Override
    public OrderItemResponse.OrderDetailResponse approvedOrderItems(UUID orderItemId) {
        OrderItem orderItem = orderItemsRepo.findById(orderItemId).orElseThrow(
                ()  -> new AppException(ErrorCode.ORDER_ITEMS_NOT_FOUND)
        );
        orderItem.setOrderItemStatus(OrderItemStatus.CONFIRMED);
        Shipment shipment = orderItem.getShipments().stream().toList().getLast();
        int cout = 0;
        for (OrderItem o : shipment.getOrderItems())
        {
            if(o.getOrderItemStatus().equals(OrderItemStatus.PENDING_CONFIRM))
            {
                cout++;
            }

        }
        if(cout == 0)
        {
            shipmentService.update(UpdateShipmentRequest.builder()
                            .shipmentId(shipment.getShipmentId())
                            .shippingStatus(ShippingStatus.PENDING)
                    .build(),null);
        }
        return orderItemMapper.entityToResponse(orderItemsRepo.save(orderItem));
    }
    @Override
    public boolean canCancleOrderDetail(UUID orderItemId) {

        Users users = getCurrentUser();
        OrderItem orderItem = orderItemsRepo.findById(orderItemId).orElseThrow(
                ()  -> new AppException(ErrorCode.ORDER_ITEMS_NOT_FOUND)
        );
        Users buyer = orderItem.getOrder().getBuyer();

        Shipment shipment =  orderItem.getShipments().stream().toList().getLast();
        return users.equals(buyer) &&
                !orderItem.getOrderItemStatus().equals(OrderItemStatus.CANCELLED)
                && shipment.getShipmentStatus() != null
                && (shipment.getShipmentStatus().equals(ShippingStatus.PENDING) || shipment.getShipmentStatus().equals(ShippingStatus.PENDING_APPROVED));
    }

    @Override
    public boolean canConfirmShipment(UUID shipmentID) {
        Users users = getCurrentUser();
        Shipment shipment = shipmentRepo.findById(shipmentID).orElseThrow(
                () -> new AppException(ErrorCode.SHIPMENT_NOT_FOUND)
        );
        Users buyer = shipment.getOrder().getBuyer();
        // TH shipment co 2 orrder 1 cai cancle 1 cai da giao toi neu lam nhu nay thi confirm duoc ca 2 a
        if(shipment.getShipmentStatus() == null ||
                (shipment.getShipmentStatus() != null &&
                        !shipment.getShipmentStatus().equals(ShippingStatus.DELIVERED))
        )
        {
            return false;
        }
        OrderItem orderItem = shipment.getOrderItems().stream().toList().getFirst();
        if(orderItem.getOrderItemStatus().equals(OrderItemStatus.RETURNING))
        {
            return users.equals(orderItem.getListSeller().getSeller()) ;
        }
        return  users.equals(buyer) ;
    }

    public Users getCurrentUser()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(email);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return users;
    }


}
