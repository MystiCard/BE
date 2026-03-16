package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.CalculateFeeRequest;
import com.example.mysterycard.dto.request.*;
import com.example.mysterycard.dto.response.OrderCardResponse;
import com.example.mysterycard.dto.response.OrderItemResponse;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.OrderItemMapper;
import com.example.mysterycard.mapper.OrderMapper;
import com.example.mysterycard.mapper.ShipmentMapper;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.NotificationService;
import com.example.mysterycard.service.ShipmentService;
import com.example.mysterycard.service.TrackingService;
import com.example.mysterycard.service.UserService;
import com.example.mysterycard.specification.ShipmentSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
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
    private final ShipmentMapper shipmentMapper;
    private final ShipmentRepo shipmentRepo;
    private final RestTemplate restTemplate = new RestTemplate();
    private final TrackingService trackingService;
    private final OrderItemsRepo orderItemsRepo;
    private final OrderRepo orderRepo;
    private final UsersRepo usersRepo;
    private final ListSellerRepo listSellerRepo;
    private final BlindBoxResultRepo blindBoxResultRepo;
    private final NotificationService notificationService;
    private final UserService userService;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;
    private final  ReturnRequestRepo returnRequestRepo;
    @Override
    public ShipmentResponse createsShipment(ShipmentRequest request) {
            Shipment shipment = shipmentMapper.requestToEntity(request);
            // create tracking
            if(request.getOrderItemId() != null && !request.getOrderItemId().isEmpty()){
                for (UUID orderItemId : request.getOrderItemId()) {
                    OrderItem orderItem = orderItemsRepo.findById(orderItemId).orElseThrow(
                         () -> new AppException(ErrorCode.ORDER_ITEMS_NOT_FOUND)
                 );
                    if(orderItem.getReturnRequest() != null)
                    {
                     ReturnRequest returnRequest = orderItem.getReturnRequest();
                     returnRequest.setShipment(shipment);
                        returnRequestRepo.save(returnRequest);
                    }else{
                        shipment.getOrderItems().add(orderItem);
                        shipment.setOrder(orderItem.getOrder());
                    }
             }
            }
            else if(request.getBlindBoxResultId() != null && !request.getBlindBoxResultId().isEmpty()) {
                for(UUID blindBoxResultId : request.getBlindBoxResultId())
                {
                    BlindBoxResult blindBoxResult = blindBoxResultRepo.findById(blindBoxResultId).orElseThrow(
                            () -> new AppException(ErrorCode.BLIND_BOX_RESULT_NOT_FOUND)
                    );
                    shipment.getBlindBoxResults().add(blindBoxResult);
                }
            }
            else {
                throw new AppException(ErrorCode.INVALID_REQUEST);
            }
            shipmentRepo.save(shipment);
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
    public List<ShipmentResponse> getShipmentByBlindBoxResult(UUID blindBoxResultId) {
        BlindBoxResult blindBoxResult = blindBoxResultRepo.findById(blindBoxResultId).orElseThrow(
                () -> new AppException(ErrorCode.BLIND_BOX_RESULT_NOT_FOUND)
        );
        return shipmentRepo.findByBlindBoxResults(Set.of(blindBoxResult)).stream().map(shipmentMapper::entityToResponse).collect(Collectors.toList());
    }
    @Override
    public Page<ShipmentResponse> myShipment(boolean complete, int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(email);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        log.info("Current User {}",users.getName());
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createAt").descending());
        List<ShippingStatus> shippingStatuses = new ArrayList<>();
        if(!complete)
        {
            shippingStatuses.addAll(List.of(ShippingStatus.ASIGNED,ShippingStatus.PICKED_UP,ShippingStatus.IN_TRANSIT,ShippingStatus.PENDING));
         }
        else {
            shippingStatuses.addAll(List.of(ShippingStatus.RECEIVED,ShippingStatus.LOST,ShippingStatus.FAILED,ShippingStatus.CANCELLED,ShippingStatus.DELIVERED));
        }

        Specification<Shipment> spec = Specification.allOf(
                ShipmentSpecification.findByUser(users),
                ShipmentSpecification.findByStatus(shippingStatuses)
        );
        return shipmentRepo.findAll(spec,pageable).map(shipmentMapper::entityToResponse);
    }

    @Override
    public Page<ShipmentResponse> shipmentNotAsigned(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createAt").descending());
        return shipmentRepo.findAllByNotHaveShipper(ShippingStatus.PENDING,pageable).map(shipmentMapper::entityToResponse);
    }

    @Override
    public ShipmentResponse recieveShipment(UUID shipemnt) {
        Users users = userService.getUser();
        Users owner = null;
        Shipment shipment = shipmentRepo.findById(shipemnt).orElseThrow(
                ()-> new AppException(ErrorCode.SHIPMENT_NOT_FOUND)
        );
        if(shipment.getOrderItems()!=null&&!shipment.getOrderItems().isEmpty())
        {
            Order order = shipment.getOrderItems().iterator().next().getOrder();
            owner = order.getBuyer();

        }
        else if(shipment.getBlindBoxResults()!=null&&!shipment.getBlindBoxResults().isEmpty())
        {
            Order order = shipment.getBlindBoxResults().iterator().next().getOrder();
            owner = order.getBuyer();

        }
        shipment.setShipper(users);
        shipment.setShipmentStatus(ShippingStatus.ASIGNED);
        notificationService.createNotification(
                "Bạn đã được giao một đơn hàng mới, vui lòng kiểm tra thông tin đơn hàng và chuẩn bị giao hàng đúng thời gian",
                users,
                Notification.NotiType.shipment
        );

        notificationService.createNotification("Đơn hàng của bạn đã được giao cho shipper "+users.getUserId().toString()+", vui lòng theo dõi để biết thông tin chi tiết về đơn hàng",owner,
                Notification.NotiType.shipment
        );
        return shipmentMapper.entityToResponse(shipmentRepo.save(shipment));
    }

    @Override
    public boolean checkAllowedRecieveShipment() {
        Users users = userService.getUser();
        boolean shipper = false;
       for(Role r : users.getRolelist())
       {
               if(r.getRoleCode().equals("SHIPPER"))
               {
                   shipper = true;
               }
       }
       if(!shipper)
       {
           throw new AppException(ErrorCode.CANNOT_IS_SHIPPER);
       }
        List<ShippingStatus> list = List.of(ShippingStatus.ASIGNED,ShippingStatus.PICKED_UP,ShippingStatus.IN_TRANSIT);
        if(shipmentRepo.existsByShipmentStatusIsInAndShipper(list,users))
        {
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public ShipmentResponse update(UpdateShipmentRequest request, List<MultipartFile> fileList) {
        Shipment shipment = shipmentRepo.findById(request.getShipmentId()).orElseThrow(
                ()-> new AppException(ErrorCode.SHIPMENT_NOT_FOUND)
        );
        Users owner = null;
        if(shipment.getOrderItems()!=null&&!shipment.getOrderItems().isEmpty())
        {
            Order order = shipment.getOrderItems().iterator().next().getOrder();
            owner = order.getBuyer();

        }
        else if(shipment.getBlindBoxResults()!=null&&!shipment.getBlindBoxResults().isEmpty())
        {
            Order order = shipment.getBlindBoxResults().iterator().next().getOrder();
            owner = order.getBuyer();

        }
        String message = switch (request.getShippingStatus()) {
            case PENDING_APPROVED -> "dang cho duyet don hang tu chu shop";
            case PENDING -> "đang chờ xử lý.";
            case ASIGNED -> "đã được gán shipper.";
            case PICKED_UP -> "đã được lấy.";
            case IN_TRANSIT -> "đang trên đường giao.";
            case DELIVERED -> "đã giao thành công.";
            case FAILED -> "giao thất bại.";
            case LOST -> "bị thất lạc.";
            case RECEIVED -> "đã được nhận.";
            case CANCELLED -> "đã bị hủy.";
        };
        notificationService.createNotification("Đơn hàng của bạn "+message,owner,
                Notification.NotiType.shipment
        );



        shipment.setShipmentStatus(request.getShippingStatus());
        shipmentRepo.save(shipment);
      ShipmentResponse shipmentResponse = shipmentMapper.entityToResponse(shipmentRepo.save(shipment));
       shipmentResponse.getTrackingResponses().add( trackingService.createTracking(
               TrackingRequest.builder()
                       .shipmentId(shipment.getShipmentId())
                       .note(request.getNote())
                       .fileList(fileList)
                       .build()));
        return shipmentResponse;
    }

    public Long getFirstServiceId( Long fromDistrict, Long toDistrict) {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/available-services";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", ghnToken);

        Map<String, Object> body = new HashMap<>();
        body.put("shop_id", Long.valueOf(shopId));
        body.put("from_district", fromDistrict);
        body.put("to_district", toDistrict);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> respBody = response.getBody();
            List<Map<String, Object>> services = (List<Map<String, Object>>) respBody.get("data");
            if (services != null && !services.isEmpty()) {
                Number serviceIdNumber = (Number) services.get(0).get("service_id");
                return serviceIdNumber.longValue(); // convert an toàn sang Long
            }

        }
        return null; // fallback nếu không có dịch vụ nào
    }
 @Override
    public Long calculatFeeShip(CalculateFeeRequest request) {

     boolean ok = false;
     long serviceId = 53320;
     do{

         try{
             HttpHeaders headers = new HttpHeaders();
             headers.set("Token",ghnToken);
             headers.set("shop_id",shopId);
             headers.setContentType(MediaType.APPLICATION_JSON);
             log.info("service id :  {}",serviceId);
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

             return Long.parseLong(data.get("total").toString());
         }catch(Exception e)
         {
            serviceId++;
         }

     }while (!ok && serviceId < 53324);
     return null;
    }
    @Transactional
    @Override
    public OrderCardResponse changeAddressShip(ChangeAddressShipmentRequest request) {

        Order order = orderRepo.findById(request.getOrderId()).orElseThrow(
                ()-> new AppException(ErrorCode.ORDER_NOT_FOUND)
        );

      List<Shipment> shipments = order.getShipmentList();
    Long shipfeeOld = 0L ;
    Long shipfeenew = 0L ;

        List<OrderItemResponse> orderItemResponses = new ArrayList<>();

        for(Shipment shipment : shipments) {
          shipfeeOld += shipment.getShipmentFee();
           ListSeller ls = shipment.getOrderItems().stream().toList().getFirst().getListSeller();
           int totalPrice = 0 ;
           for (OrderItem orderItem : shipment.getOrderItems())
           {
                       totalPrice += orderItem.getQuantity() * orderItem.getPrice();

           }
            Long shipfee = calculatFeeShip(
                    CalculateFeeRequest.builder()
                            .totalAmount(totalPrice)
                            .fromDistrictId(Long.valueOf(ls.getSeller().getDistrictId()))
                            .toWardId(String.valueOf(request.getToWardId()))
                            .toDistrictId(request.getToDistrictId())
                            .build()
            );
            shipfeenew+= shipfee;
            shipment.setToAddress(request.getNewAddress());
            shipment.setShipmentFee(shipfee);
            shipment.setToWardId(request.getToWardId());
            shipment.setToDistrictId(request.getToDistrictId());
            shipment.setToName(request.getToName());
            shipment.setToPhone(request.getToPhone());
            OrderItemResponse orderItemResponse = OrderItemResponse.builder()
                    .orderDetailResponseList(shipment.getOrderItems().stream().map(orderItemMapper::entityToResponse).toList())
                    .shipfee(shipfee)
                    .shipmentResponse(shipmentMapper.entityToResponse(shipment))
                    .build();
            orderItemResponses.add(orderItemResponse);
      }

        order.setTotalAmount(order.getTotalAmount()-(shipfeeOld-shipfeenew));
        orderRepo.save(order);


        OrderCardResponse orderCardResponse = orderMapper.entityToResponse(order);
        orderCardResponse.setOrderItems(orderItemResponses);

        return   orderCardResponse;
    }
}
