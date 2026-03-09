package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.ReturnRequestdto;
import com.example.mysterycard.dto.response.PageResponse;
import com.example.mysterycard.dto.response.ReturnResponse;
import com.example.mysterycard.enums.ReturnRequestStatus;
import com.example.mysterycard.service.ReturnRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/return-requests")
@RestController
@RequiredArgsConstructor
public class RequestReturnController {
    private final ReturnRequestService returnRequestService;

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReturnResponse>> sendReturnRequest(
         @Valid @RequestPart ReturnRequestdto request,
            @RequestPart List<MultipartFile> files
    ) {
        return ResponseEntity.ok(ApiResponse.success(returnRequestService.sendReturnRequest(request, files)));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<Page<ReturnResponse>>> getMySendReturnRequest(
            @RequestParam(required = false) ReturnRequestStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(returnRequestService.getMySendReturnRequest(status, page, size)));
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<PageResponse<ReturnResponse>>> receiveReturnRequest(
            @RequestParam(required = false) ReturnRequestStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(returnRequestService.receiveReturnRequest(status, page, size)));
    }
    @PostMapping("/approve/{returnRequestId}")
    public ResponseEntity<ApiResponse<ReturnResponse>> approveReturnRequest(
            @PathVariable UUID returnRequestId
    ) {
        return ResponseEntity.ok(ApiResponse.success(returnRequestService.approveReturnRequest(returnRequestId)));
    }
    @PostMapping("cancel/{returnRequestId}")
    public ResponseEntity<ApiResponse<ReturnResponse>> cancelReturnRequest(
            @PathVariable UUID returnRequestId
    ) {
        return ResponseEntity.ok(ApiResponse.success(returnRequestService.cancleReturnRequest(returnRequestId)));
    }
    @GetMapping("/{shipmentId}/can-return")
    public ResponseEntity<ApiResponse<?>> canreturn(@PathVariable UUID shipmentId){
        return ResponseEntity.ok(ApiResponse.success(returnRequestService.canSendReturn(shipmentId)));
    }
    @GetMapping("/reject/{returnId}")
    public ResponseEntity<ApiResponse<?>> reject(@PathVariable UUID returnId){
        return ResponseEntity.ok(ApiResponse.success(returnRequestService.rejectReturnReqeust(returnId)));
    }
    @GetMapping("/can-do/{returnId}")
    public ResponseEntity<ApiResponse<?>> canPayment(@PathVariable UUID returnId){
        return ResponseEntity.ok(ApiResponse.success(returnRequestService.canDo(returnId)));
    }
}
