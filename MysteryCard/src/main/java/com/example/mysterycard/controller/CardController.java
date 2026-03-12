package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.*;
import com.example.mysterycard.dto.response.CardResponse;
import com.example.mysterycard.dto.response.CardSellResponse;
import com.example.mysterycard.dto.response.PageResponse;
import com.example.mysterycard.enums.Rarity;
import com.example.mysterycard.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
@Slf4j
public class CardController {
    private final CardService cardService;

    @GetMapping
    public ApiResponse getAllCards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "asc") String sort

    ) {
        return ApiResponse.success(cardService.getAllCards(page, size, keyword, sort));
    }

    @GetMapping("/{id}")
    public ApiResponse getCardById(@PathVariable UUID id) {
        return ApiResponse.success(cardService.getCardById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse createCard(@RequestPart CardRequest request, @RequestPart MultipartFile file) {
        return ApiResponse.success(cardService.createCard(request, file));
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteCard(@PathVariable UUID id) {
        cardService.deleteCardById(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse updateCard(@PathVariable UUID id, @RequestBody CardRequest request) {
        return ApiResponse.success(cardService.updateCard(id, request));
    }

    @PostMapping("/import")
    public ApiResponse importCategories(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(cardService.importCards(file));
    }

    @PostMapping("/wishlist/{cardId}")
    public ApiResponse addToWishList(@PathVariable UUID cardId, @RequestBody WishListRequest request) {
        return ApiResponse.success(cardService.addToWishList(cardId, request));
    }

    @DeleteMapping("/wishlist/{wishListId}")
    public ApiResponse removeFromWishList(@PathVariable UUID wishListId) {
        cardService.removeFromWishList(wishListId);
        return ApiResponse.success();
    }

    @PutMapping("/wishlist/{wishListId}")
    public ApiResponse changeExpectPrice(@PathVariable UUID wishListId, @RequestParam Long newExpectPrice) {
        return ApiResponse.success(cardService.changeExpectPrice(wishListId, newExpectPrice));
    }

    @GetMapping("/wishlist")
    public ApiResponse getUserWishList(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(cardService.getUserWishList(page, size));
    }

    @PostMapping(value = "/required", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse requiredNewCard(@RequestPart NewCardRequest request, @RequestPart MultipartFile file) {
        return ApiResponse.success(cardService.requireNewCard(request, file));
    }

    @PutMapping("{requirdId}/reject")
    public ApiResponse rejectRequiredNewCard(@RequestBody AddCardRequest request, @PathVariable UUID requirdId) {
        return ApiResponse.success(cardService.rejectRequest(request, requirdId));
    }

    @PutMapping(value = "{requirdId}/approve", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse approveRequiredNewCard(
            @RequestPart AddCardRequest request,
            @PathVariable UUID requirdId,
            @RequestPart MultipartFile file
    ) {
        return ApiResponse.success(cardService.approveRequest(request, requirdId,file));
    }

    @GetMapping("/required-user")
    public ApiResponse getAllRequiredCardByUser(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(cardService.getAllRequiredByUsers(page, size));
    }

    @GetMapping("/required-admin")
    public ApiResponse getAllRequiredCards(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(cardService.gettAllRequireds(page, size));
    }
    @PostMapping("/search-image")
    public ResponseEntity<ApiResponse<List<CardResponse>>> searchByImage(@RequestParam MultipartFile file) {
return ResponseEntity.ok(ApiResponse.success(cardService.searchByImage(file)));
    }
    @PostMapping ("/card-selling")
    public ResponseEntity<ApiResponse<PageResponse<CardSellResponse>>> getCardSelling(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody CardSellRequest request
            ) {
        return ResponseEntity.ok(ApiResponse.success(cardService.getALlCarSelling(page,size,request)));

    }

}
