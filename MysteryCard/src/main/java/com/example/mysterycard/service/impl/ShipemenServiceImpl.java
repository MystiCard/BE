package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.CalculateFeeRequest;
import com.example.mysterycard.dto.request.*;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.ShipmentMapper;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.ShipmentService;
import com.example.mysterycard.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipemenServiceImpl implements ShipmentService {
    @Value("${ghn.token}")
    private String ghnToken;
    @Value("${ghn.shopId}")
    private String shopId;
    @Value("${ghn.url-fee}")
    private String url_fee;
    @Value("${ghn.length}")
    private   int length;
    @Value("${ghn.height}")
    private   int height;
    @Value("${ghn.weight}")
    private   int weight;
    @Value("${ghn.width}")
    private int width;
    @Value("${ghn.service_id}")
    private   Long serviceId;
    private final ShipmentMapper shipmentMapper;
    private final ShipmentRepo shipmentRepo;
    private final RestTemplate restTemplate = new RestTemplate();
    private final TrackingService trackingService;
    private final OrderItemsRepo orderItemsRepo;
    private final OrderRepo orderRepo;
    private final UsersRepo usersRepo;
    private final ListSellerRepo listSellerRepo;
    @Override
    public ShipmentResponse createsShipment(ShipmentRequest request) {
        OrderItem orderItem = orderItemsRepo.findById(request.getOrderItemId()).orElseThrow(
                () -> new AppException(ErrorCode.ORDER_ITEMS_NOT_FOUND)
        );
      Shipment shipment = shipmentMapper.requestToEntity(request);
      shipment.getOrderItems().add(orderItem);
        // create tracking
        trackingService.createTracking(
                TrackingRequest.builder()
                        .shipmentId(shipment.getShipmentId())
                .build());
        return shipmentMapper.entityToResponse(shipment);
    }

    @Override
    @Transactional
    public List<ShipmentResponse> getShipmentByOrderItems(UUID orderItemId) {
        OrderItem orderItem = orderItemsRepo.findById(orderItemId).orElseThrow(
                () -> new AppException(ErrorCode.ORDER_ITEMS_NOT_FOUND)
        );
        return shipmentRepo.findByOrderItems(Set.of(orderItem)).stream().map(shipmentMapper::entityToResponse).collect(Collectors.toList());
    }

    @Override
    public Page<ShipmentResponse> myShipment(boolean complete, int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(email);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createAt").descending());
        List<ShippingStatus> shippingStatuses = new ArrayList<>(Arrays.stream(ShippingStatus.values()).toList());
        if(!complete)
        {
            shippingStatuses.removeAll(List.of(ShippingStatus.PENDING,ShippingStatus.PICKED_UP,ShippingStatus.IN_TRANSIT,ShippingStatus.ASIGNED));
        }
        else {
            shippingStatuses.removeAll(List.of(ShippingStatus.RETURNED,ShippingStatus.DELIVERED,ShippingStatus.LOST));
        }
        return shipmentRepo.findAllByShipmentStatusNotInAndShipper(shippingStatuses,users,pageable).map(shipmentMapper::entityToResponse);
    }

    @Override
    public Page<ShipmentResponse> shipmentNotAsigned(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createAt").descending());
        return shipmentRepo.findAllByNotHaveShipper(ShippingStatus.PENDING,pageable).map(shipmentMapper::entityToResponse);
    }

    @Override
    public ShipmentResponse asignShipper(AsignShipperRequest request) {
        Users users = usersRepo.findByUserId(request.getShipperId());
        if(users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Shipment shipment = shipmentRepo.findById(request.getShipmentId()).orElseThrow(
                ()-> new AppException(ErrorCode.SHIPMENT_NOT_FOUND)
        );
        shipment.setShipper(users);
        shipment.setShipmentStatus(ShippingStatus.ASIGNED);

        return shipmentMapper.entityToResponse(shipmentRepo.save(shipment));
    }

    @Override
    public ShipmentResponse update(UpdateShipmentRequest request, List<MultipartFile> fileList) {
        Shipment shipment = shipmentRepo.findById(request.getShipmentId()).orElseThrow(
                ()-> new AppException(ErrorCode.SHIPMENT_NOT_FOUND)
        );
        shipment.setShipmentStatus(request.getShippingStatus());
        trackingService.createTracking(
                TrackingRequest.builder()
                        .shipmentId(shipment.getShipmentId())
                        .note(request.getNote())
                        .fileList(fileList)
                        .build());

        return shipmentMapper.entityToResponse(shipmentRepo.save(shipment));
    }
 @Override
    public Long calculatFeeShip(CalculateFeeRequest request) {
        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.set("Token",ghnToken);
        headers.set("shop_id",shopId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        CalculateShipmentFeeRequest calRequest = CalculateShipmentFeeRequest.builder()
                .service_id(serviceId)
                .insurance_value(Math.round(request.getTotalAmount()))
                .coupon(null)
                .from_district_id(request.getFromDistrictId())
                .to_district_id(request.getToDistrictId())
                .to_ward_code(request.getToWardId())
                .height(height)
                .length(length)
                .weight(weight)
                .width(width)
                .build();
        HttpEntity<CalculateShipmentFeeRequest> entity = new HttpEntity<>(calRequest, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                url_fee,
                entity,
                Map.class
        );
        log.info("Response {}", response.getBody());
     Map<String, Object> body = response.getBody();

     Map<String, Object> data = (Map<String, Object>) body.get("data");

     Long total = Long.parseLong(data.get("total").toString());

     return total;
    }
    @Transactional
    @Override
    public Long changeAddressShip(ChangeAddressShipmentRequest request) {

        Order order = orderRepo.findById(request.getOrderId()).orElseThrow(
                ()-> new AppException(ErrorCode.ORDER_NOT_FOUND)
        );

        ListSeller ls = listSellerRepo.findById(request.getListsellerId())
                .orElseThrow(() -> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND));
        Long shipfee = calculatFeeShip(
                CalculateFeeRequest.builder()
                        .totalAmount(request.getTotalPrice())
                        .fromDistrictId(Long.valueOf(ls.getSeller().getDistrictId()))
                        .toWardId(String.valueOf(request.getToWardId()))
                        .toDistrictId(request.getToDistrictId())
                        .build()
        );
        order.setTotalAmount(order.getTotalAmount()-request.getOldShipmentFee()+shipfee);
        orderRepo.save(order);
        return shipfee;
    }
}
