package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.AsignShipperRequest;
import com.example.mysterycard.dto.request.UpdateShipmentRequest;
import com.example.mysterycard.dto.request.user.UserRegisterRequest;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {
    private final ShipmentService shipmentService;
    @GetMapping("/orders/{orderid}")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> findShipmentsByOrderId(
            @PathVariable("orderid") UUID orderid) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.getShipmentByOrder(orderid)));
    }
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<ShipmentResponse>>> getMeShipments(
            @RequestParam(required = false,defaultValue = "false") boolean complete,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.myShipment(complete,page,size)));
    }
    @GetMapping("/not-asign")
    public ResponseEntity<ApiResponse<Page<ShipmentResponse>>> getNotAsignedShipments(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return ResponseEntity.ok(ApiResponse.success(shipmentService.shipmentNotAsigned(page,size)));
    }
    @PostMapping("/asign-shipper")
    public ResponseEntity<ApiResponse<ShipmentResponse>> assignShipper(
            @RequestBody AsignShipperRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(shipmentService.asignShipper(request)));
    }
    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipment(
            @RequestPart @Valid UpdateShipmentRequest request,
            @RequestPart(required = false) List<MultipartFile> fileList
    ){
        return ResponseEntity.ok(ApiResponse.success(shipmentService.update(request,fileList)));
    }
}
