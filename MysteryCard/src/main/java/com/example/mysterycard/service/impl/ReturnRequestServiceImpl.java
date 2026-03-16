package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.*;
import com.example.mysterycard.dto.response.*;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.OrderItemStatus;
import com.example.mysterycard.enums.ReturnRequestStatus;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.ReturnRequestMapper;
import com.example.mysterycard.mapper.ShipmentMapper;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.ReturnRequestService;
import com.example.mysterycard.service.ShipmentService;
import com.example.mysterycard.service.TransactionService;
import com.example.mysterycard.service.UserService;
import com.example.mysterycard.utils.CloudiaryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReturnRequestServiceImpl implements ReturnRequestService {
    private final ReturnRequestMapper returnRequestMapper;
    private final OrderItemsRepo orderItemsRepo;
    private final ReturnRequestRepo returnRequestRepo;
    private final UsersRepo usersRepo;
    private final ImageRepo imageRepo;
    private final CloudiaryUtils cloudiaryUtils;
    private final ShipmentService shipmentService;
    private final TransactionService transactionService;
    private final ShipmentRepo shipmentRepo;
    private final UserService userService;

    private final ShipmentMapper shipmentMapper;
    @Value("${DAY_CAN_RETURN}")
    private Long dayCanReturn;

    @Override
    public ReturnResponse cancleReturnRequest(UUID returnRequestId) {
        ReturnRequest returnRequest = returnRequestRepo.findById(returnRequestId).orElseThrow(
                () -> new AppException(ErrorCode.RETURN_REQUEST_NOT_FOUND)
        );
        Shipment shipment = returnRequest.getShipment();

        if (returnRequest.getStatus().equals(ReturnRequestStatus.PAID) && shipment.getShipmentStatus().equals(ShippingStatus.PENDING)) {
            transactionService.refundCancleReturn(returnRequest);
        }
        shipmentService.update(UpdateShipmentRequest.builder()
                .shippingStatus(ShippingStatus.CANCELLED)
                .shipmentId(shipment.getShipmentId())
                .build(), null);
        updateStatusOrderItem(returnRequest,OrderItemStatus.RECIEVED);
        returnRequest.setStatus(ReturnRequestStatus.CANCELED);
        return returnRequestMapper.enityToReturnResponse(returnRequestRepo.save(returnRequest));
    }

    public void updateStatusOrderItem(ReturnRequest request, OrderItemStatus status) {
        for (OrderItem orderItem : request.getOrderItemList()) {
            orderItem.setOrderItemStatus(status);

        }
        orderItemsRepo.saveAll(request.getOrderItemList());
    }

    @Override
    @Transactional
    public ReturnResponse sendReturnRequest(ReturnRequestdto request, List<MultipartFile> fileList) {
        ReturnRequest returnRequest = returnRequestMapper.requestToEntity(request);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users buyer = usersRepo.findByEmail(email);
        if (buyer == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        returnRequest.setBuyer(buyer);
        List<UUID> orderItemIds = new ArrayList<>();
        double totalAmount = 0;
        Users seller = null;
        for (UUID orderItemId : request.getOrderItemIds()) {
            OrderItem orderItem = orderItemsRepo.findById(orderItemId).orElseThrow(
                    () -> new AppException(ErrorCode.ORDER_ITEMS_NOT_FOUND));
            if (!orderItem.getOrderItemStatus().equals(OrderItemStatus.RECIEVED)) {
                throw new AppException(ErrorCode.CAN_NOT_SEND_RETURN_REQUEST);
            }
            orderItem.setOrderItemStatus(OrderItemStatus.RETURNING);
            orderItem.setReturnRequest(returnRequestRepo.save(returnRequest));
            orderItemsRepo.save(orderItem);
            returnRequest.getOrderItemList().add(orderItem);
            totalAmount += orderItem.getPrice() * orderItem.getQuantity();
            orderItemIds.add(orderItemId);
            seller = orderItem.getListSeller().getSeller();
        }
        // tao shipment cho return request
        Long shipfee = shipmentService.calculatFeeShip(CalculateFeeRequest.builder()
                .totalAmount(totalAmount)
                .fromDistrictId(request.getSendDistrictId())
                .toDistrictId(Long.valueOf(seller.getDistrictId()))
                .toWardId(seller.getWardId())
                .build());
        ShipmentResponse shipmentResponse = shipmentService.createsShipment(ShipmentRequest.builder()
                .orderItemId(orderItemIds)
                .fromDistrictId(request.getSendDistrictId())
                .fromPhone(request.getSendPhone())
                .fromAddress(request.getSendAddress())
                .shipmentFee(shipfee)
                .toAddress(seller.getAddress())
                .toDistrictId(Long.valueOf(seller.getDistrictId()))
                .toWardId(Long.valueOf(seller.getWardId()))
                .toPhone(seller.getPhone())
                        .fromName(request.getSendName())
                        .toName(seller.getName())
                .build()
        );
        for (MultipartFile file : fileList) {
            Image image = Image.builder()
                    .returnRequest(returnRequest)
                    .imageUrl(cloudiaryUtils.uploadImage(file))
                    .build();
            imageRepo.save(image);
            returnRequest.getImages().add(image);
        }
        ReturnResponse response = returnRequestMapper.enityToReturnResponse(returnRequest);
        response.setShipmentResponse(shipmentResponse);
        return response;
    }

    @Override
    public Page<ReturnResponse> getMySendReturnRequest(ReturnRequestStatus status, int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users buyer = usersRepo.findByEmail(email);
        if (buyer == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Page<ReturnRequest> returnRequestPage = null;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        if (status == null) {
            returnRequestPage = returnRequestRepo.findByBuyer(buyer, pageable);
            return returnRequestPage.map(returnRequestMapper::enityToReturnResponse);
        }

        returnRequestPage = returnRequestRepo.findByBuyerAndStatus(buyer, status, pageable);
        return returnRequestPage.map(returnRequestMapper::enityToReturnResponse);
    }

    @Override
    public PageResponse<ReturnResponse> receiveReturnRequest(ReturnRequestStatus status, int page, int size) {
        page = page - 1;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users seller = usersRepo.findByEmail(email);
        if (seller == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        List<OrderItem> orderItemList = orderItemsRepo.findByListSeller_Seller(seller);
        Set<ReturnRequest> returnRequests = null;
        if (status == null) {
            returnRequests = orderItemList.stream().filter(orderItem -> orderItem.getReturnRequest() != null)
                    .map(OrderItem::getReturnRequest).collect(java.util.stream.Collectors.toSet());
        } else {
            returnRequests = orderItemList.stream().filter(orderItem -> orderItem.getReturnRequest() != null
                            && orderItem.getReturnRequest().getStatus().equals(status))
                    .map(OrderItem::getReturnRequest).collect(java.util.stream.Collectors.toSet());
        }
        returnRequests.stream().sorted(Comparator.comparing(ReturnRequest::getCreatedAt).reversed()) ;
        int totalPages = returnRequests.size() / size;
        int from = page * size;
        int to = Math.min(((page + 1) * size), returnRequests.size());
        List<ReturnRequest> returns = new ArrayList<>();
        if (from < to) {
            returns = returnRequests.stream().toList().subList(from, to);
        }

        PageResponse<ReturnResponse> pageResponse = PageResponse.<ReturnResponse>builder()
                .content(returns.stream().map(returnRequestMapper::enityToReturnResponse).toList())
                .totalElements(returnRequests.size())
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .last(to == returnRequests.size())
                .build();
        return pageResponse;
    }

    @Override
    public ReturnResponse approveReturnRequest(UUID returnRequestId) {
        ReturnRequest returnRequest = returnRequestRepo.findById(returnRequestId).orElseThrow(
                () -> new AppException(ErrorCode.RETURN_REQUEST_NOT_FOUND)
        );
        if (!returnRequest.getStatus().equals(ReturnRequestStatus.REQUESTED)) {
            throw new AppException(ErrorCode.CAN_NOT_APPROVE_RETURN_REQUEST);
        }
        returnRequest.setStatus(ReturnRequestStatus.APPROVED);
        returnRequestRepo.save(returnRequest);
        return returnRequestMapper.enityToReturnResponse(returnRequest);
    }

  @Override
    public boolean canSendReturn(UUID shipmentId  ) {
        Shipment shipment = shipmentRepo.findById(shipmentId).orElseThrow(
                () -> new AppException(ErrorCode.SHIPMENT_NOT_FOUND)
        );
        Users currentUser = userService.getUser();
        Set<OrderItem> orderItemList = shipment.getOrderItems();
        int count =0 ;
        for(OrderItem orderItem : orderItemList)
        {
            if(orderItem.getOrderItemStatus().equals(OrderItemStatus.RECIEVED)){
                count++;
            }
        }
        if(count == 0)
        {
            return false;
        }

        Tracking tracking = shipment.getTrackingList().getLast();
        return shipment.getShipmentStatus() != null && shipment.getShipmentStatus().equals(ShippingStatus.RECEIVED)
                && tracking.getCreateAt().plusDays(dayCanReturn).isAfter(LocalDateTime.now())
                ;
    }

    @Override

    public ReturnResponse rejectReturnReqeust(UUID retrurnRequestId) {
        ReturnRequest returnRequest = returnRequestRepo.findById(retrurnRequestId).orElseThrow(
                () -> new AppException(ErrorCode.RETURN_REQUEST_NOT_FOUND)
        );
        if (!returnRequest.getStatus().equals(ReturnRequestStatus.REQUESTED)) {
            throw new AppException(ErrorCode.CAN_NOT_APPROVE_RETURN_REQUEST);
        }
        returnRequest.setStatus(ReturnRequestStatus.REJECTED);
        updateStatusOrderItem(returnRequest,OrderItemStatus.REJECT_RETURN);
        returnRequestRepo.save(returnRequest);
        return returnRequestMapper.enityToReturnResponse(returnRequest);
    }


    public boolean canCancle(UUID returnReqeustId, Users users) {

        ReturnRequest returnRequest = returnRequestRepo.findById(returnReqeustId).orElseThrow(
                () -> new AppException(ErrorCode.RETURN_REQUEST_NOT_FOUND)
        );

        Shipment shipment = returnRequest.getShipment();

        return returnRequest.getBuyer().equals(users) &&
                (
                        shipment.getShipmentStatus() == null || (shipment.getShipmentStatus() != null
                                && shipment.getShipmentStatus().equals(ShippingStatus.PENDING))
                );
    }

    public boolean canPayforReturnRequest(UUID returnRequestId ,  Users users ) {
        ReturnRequest returnRequest = returnRequestRepo.findById(returnRequestId).orElseThrow(
                () -> new AppException(ErrorCode.RETURN_REQUEST_NOT_FOUND)
        );
        return returnRequest.getCreatedAt().plusDays(7).isAfter(LocalDateTime.now()) &&
                returnRequest.getBuyer().equals(users) &&
                returnRequest.getStatus().equals(ReturnRequestStatus.APPROVED)
                ;
    }
    public boolean canRejectOrApproved(ReturnRequest returnRequest,Users users) {
        if(returnRequest.getOrderItemList() != null && returnRequest.getOrderItemList().size() > 0) {
            Users seller = returnRequest.getOrderItemList().getFirst().getListSeller().getSeller();

            return seller.equals(users) && returnRequest.getStatus().equals(ReturnRequestStatus.REQUESTED);
        }
        return  false;
    }

    @Override
    public RetrunRequestCanDoResponse canDo(UUID returnRequestId) {
        Users users = usersRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        ReturnRequest returnRequest = returnRequestRepo.findById(returnRequestId).orElseThrow(
                () -> new AppException(ErrorCode.RETURN_REQUEST_NOT_FOUND)
        );
        RetrunRequestCanDoResponse response = RetrunRequestCanDoResponse.builder()
                .canCancle(canCancle(returnRequestId,users))
                .canjectOrApproved(canRejectOrApproved(returnRequest,users))
                .canPayment(canPayforReturnRequest(returnRequestId,users))
                .canConfirmRecieved(canConfirm(returnRequest,users))
                .build();
        return response;
    }
    public boolean canConfirm(ReturnRequest request, Users users) {
        if(request.getOrderItemList() != null && request.getOrderItemList().size() > 0) {
            Users seller = request.getOrderItemList().getFirst().getListSeller().getSeller();
            Shipment shipment = request.getShipment();
           return users.equals(seller) && shipment.getShipmentStatus() != null && shipment.getShipmentStatus().equals(ShippingStatus.DELIVERED);
        }
        return  false;

    }
    @Override
    @Transactional
    public ReturnResponse confirmRecieved(UUID returnId) {
        ReturnRequest returnRequest = returnRequestRepo.findById(returnId).orElseThrow(
                () -> new AppException(ErrorCode.RETURN_REQUEST_NOT_FOUND)
        );
        returnRequest.setStatus(ReturnRequestStatus.COMPLETE);
        List<OrderItem> orderItemList = returnRequest.getOrderItemList();
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderItemStatus(OrderItemStatus.RETURNED);
            orderItemsRepo.save(orderItem);
            transactionService.releasePrice(orderItem);
        }
        returnRequest.getShipment().setShipmentStatus(ShippingStatus.RECEIVED);
        return returnRequestMapper.enityToReturnResponse(returnRequestRepo.save(returnRequest));
    }

}
