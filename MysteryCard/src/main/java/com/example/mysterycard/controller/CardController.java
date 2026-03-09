package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.AddCardRequest;
import com.example.mysterycard.dto.request.CardRequest;
import com.example.mysterycard.dto.request.NewCardRequest;
import com.example.mysterycard.dto.request.WishListRequest;
import com.example.mysterycard.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam(required = false) String  keyword,
            @RequestParam(required = false,defaultValue = "asc") String  sort

    ) {
        return ApiResponse.success(cardService.getAllCards(page,size,keyword,sort));
    }

    @GetMapping("/{id}")
    public ApiResponse getCardById(@PathVariable UUID id) {
        return ApiResponse.success(cardService.getCardById(id));
    }
    @PostMapping()
    public ApiResponse createCard(@RequestBody CardRequest request){
        return ApiResponse.success(cardService.createCard(request));
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
    @PostMapping("/wishlist/{cardId}" )
    public ApiResponse addToWishList(@PathVariable UUID cardId, @RequestBody WishListRequest request){
        return ApiResponse.success(cardService.addToWishList(cardId, request));
    }
    @DeleteMapping("/wishlist/{wishListId}" )
    public ApiResponse removeFromWishList(@PathVariable UUID wishListId){
        cardService.removeFromWishList(wishListId);
        return ApiResponse.success();
    }
    @PutMapping("/wishlist/{wishListId}" )
    public ApiResponse changeExpectPrice(@PathVariable UUID wishListId, @RequestParam Long newExpectPrice){
        return ApiResponse.success(cardService.changeExpectPrice(wishListId, newExpectPrice));
    }
    @GetMapping("/wishlist")
    public ApiResponse getUserWishList(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size ) {
        return ApiResponse.success(cardService.getUserWishList(page,size));
    }

    @PostMapping("/required")
    public ApiResponse requiredNewCard(@RequestBody NewCardRequest request){
        return ApiResponse.success(cardService.requireNewCard(request));
    }

    @PutMapping("{requirdId}/reject")
    public ApiResponse rejectRequiredNewCard(@RequestBody AddCardRequest request, @PathVariable UUID requirdId){
        return ApiResponse.success(cardService.rejectRequest(request,requirdId));
    }
    @PutMapping("{requirdId}/approve")
    public ApiResponse approveRequiredNewCard(@RequestBody AddCardRequest request, @PathVariable UUID requirdId){
        return ApiResponse.success(cardService.approveRequest(request,requirdId));
    }

    @GetMapping("/required-user")
    public ApiResponse getAllRequiredCardByUser(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size ) {
        return ApiResponse.success(cardService.getAllRequiredByUsers(page,size));
    }
    @GetMapping("/required-admin")
    public ApiResponse getAllRequiredCards(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size ) {
        return ApiResponse.success(cardService.gettAllRequireds(page,size));
    }

}
