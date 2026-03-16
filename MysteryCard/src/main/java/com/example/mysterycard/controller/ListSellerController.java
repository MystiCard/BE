package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.SellRequest;
import com.example.mysterycard.dto.response.ListSellerResponse;
import com.example.mysterycard.service.ListSellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/listseller")
public class ListSellerController {
    @Autowired
    private ListSellerService listSellerService;

    @PostMapping("/{id}")
    public ApiResponse createListSeller(@PathVariable UUID id, @RequestBody SellRequest request) {
        return ApiResponse.success(listSellerService.createListSeller(request, id));
    }

    @GetMapping("/{id}")
    public ApiResponse getListSellersByCardId(@PathVariable UUID id,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(listSellerService.getListSellersByCardId(id, page, size));
    }

    @GetMapping("/sell-listings/{userId}")
    public ApiResponse<Page<ListSellerResponse>> getMyList(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size) {
      return ApiResponse.success( listSellerService.getListingByUserId(userId,page, size));

    }
}
