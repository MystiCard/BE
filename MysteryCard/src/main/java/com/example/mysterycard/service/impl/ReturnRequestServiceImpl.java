package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.*;
import com.example.mysterycard.dto.response.OrderItemResponse;
import com.example.mysterycard.dto.response.PageResponse;
import com.example.mysterycard.dto.response.ReturnResponse;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.OrderItemStatus;
import com.example.mysterycard.enums.ReturnRequestStatus;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.ReturnRequestMapper;
import com.example.mysterycard.repository.ImageRepo;
import com.example.mysterycard.repository.OrderItemsRepo;
import com.example.mysterycard.repository.ReturnRequestRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.ReturnRequestService;
import com.example.mysterycard.service.ShipmentService;
import com.example.mysterycard.service.TransactionService;
import com.example.mysterycard.utils.CloudiaryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReturnRequestServiceImpl implements ReturnRequestService {
    private final ReturnRequestMapper returnRequestMapper;
    private final OrderItemsRepo orderItemsRepo;
    private final ReturnRequestRepo returnRequestRepo;
    private final UsersRepo usersRepo;
    private final ImageRepo imageRepo;
    private final CloudiaryUtils cloudiaryUtils;
    private final ShipmentService shipmentService;
    private final TransactionService transactionService;

    @Override
    public ReturnResponse cancleReturnRequest(UUID returnRequestId) {
        ReturnRequest returnRequest = returnRequestRepo.findById(returnRequestId).orElseThrow(
                () -> new AppException(ErrorCode.RETURN_REQUEST_NOT_FOUND)
        );
        Shipment shipment = returnRequest.getOrderItemList().getLast().getShipments().stream().toList().getLast();
        if(returnRequest.getStatus().equals(ReturnRequestStatus.PAID) && shipment.getShipmentStatus().equals(ShippingStatus.PENDING))
        {
            transactionService.refundCancleReturn(returnRequest);
            shipmentService.update(UpdateShipmentRequest.builder()
                    .shippingStatus(ShippingStatus.CANCELLED)
                    .shipmentId(shipment.getShipmentId())
                    .build(), null);
        }
        updateStatusOrderItem(returnRequest);
        returnRequest.setStatus(ReturnRequestStatus.CANCELED);
        return returnRequestMapper.enityToReturnResponse(returnRequestRepo.save(returnRequest));
    }
    public void updateStatusOrderItem(ReturnRequest request)
    {
        for (OrderItem orderItem : request.getOrderItemList()) {
            orderItem.setOrderItemStatus(OrderItemStatus.RECIEVED);
            orderItemsRepo.save(orderItem);
        }
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
            if(!orderItem.getOrderItemStatus().equals(OrderItemStatus.RECIEVED))
            {
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
      ShipmentResponse  shipmentResponse = shipmentService.createsShipment(ShipmentRequest.builder()
                .orderItemId(orderItemIds)
                .fromDistrictId(request.getSendDistrictId())
                .fromPhone(request.getSendPhone())
                .fromAddress(request.getSendAddress())
                .shipmentFee(shipfee)
                .toAddress(seller.getAddress())
                .toDistrictId(Long.valueOf(seller.getDistrictId()))
                .toWardId(Long.valueOf(seller.getWardId()))
                .toPhone(seller.getPhone())
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
        if(!returnRequest.getStatus().equals(ReturnRequestStatus.REQUESTED))
        {
            throw new AppException(ErrorCode.CAN_NOT_APPROVE_RETURN_REQUEST);
        }
        returnRequest.setStatus(ReturnRequestStatus.APPROVED);
        returnRequestRepo.save(returnRequest);
        return returnRequestMapper.enityToReturnResponse(returnRequest);
    }
}
