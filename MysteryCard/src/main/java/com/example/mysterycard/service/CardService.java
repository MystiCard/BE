package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.*;
import com.example.mysterycard.dto.response.*;
import com.example.mysterycard.enums.Rarity;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CardService {
    CardResponse getCardById(UUID id);
     Page<CardResponse> getAllCards(int page, int size,String search , String sort);
    void deleteCardById(UUID id);
    CardResponse createCard(CardRequest request,MultipartFile file) ;
    CardResponse updateCard(UUID id, CardRequest request);
    Map<String, Integer> importCards(MultipartFile file);
    Page<WishListResponse> getUserWishList(int page , int size);
    WishListResponse changeExpectPrice(UUID wishListId, Long newExpectPrice);
    void removeFromWishList(UUID wishListId);
    WishListResponse addToWishList(UUID cardId, WishListRequest request);

    CardRequiredResponse requireNewCard(NewCardRequest request,MultipartFile file);
    CardRequiredResponse approveRequest(AddCardRequest request, UUID cardRequestId,MultipartFile file);
    CardRequiredResponse rejectRequest(AddCardRequest request, UUID cardRequestId);
    Page<CardRequiredResponse> getAllRequiredByUsers(int page , int size);
    Page<CardRequiredResponse> gettAllRequireds ( int page , int size);
    List<CardResponse> searchByImage(MultipartFile file);
    PageResponse<CardSellResponse> getALlCarSelling(int page, int size, CardSellRequest request);


}
