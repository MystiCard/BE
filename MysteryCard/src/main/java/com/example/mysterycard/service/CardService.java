package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.CardRequest;
import com.example.mysterycard.dto.request.WishListRequest;
import com.example.mysterycard.dto.response.CardResponse;
import com.example.mysterycard.dto.response.WishListResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CardService {
    CardResponse getCardById(UUID id);
    List<CardResponse> getAllCards();
    void deleteCardById(UUID id);
    CardResponse createCard(CardRequest request);
    CardResponse updateCard(UUID id, CardRequest request);
    Map<String, Integer> importCards(MultipartFile file);
    Page<WishListResponse> getUserWishList(int page , int size);
    WishListResponse changeExpectPrice(UUID wishListId, Long newExpectPrice);
    void removeFromWishList(UUID wishListId);
    WishListResponse addToWishList(UUID cardId, WishListRequest request);
}
