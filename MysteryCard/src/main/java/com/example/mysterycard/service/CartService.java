package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.CartRequest;
import com.example.mysterycard.dto.response.CartResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CartService  {
    CartResponse addToCard(CartRequest request);
    CartResponse update(CartRequest request,UUID cartId);
    void remove(UUID cartId);
    Page<CartResponse> getALl(int page, int size);
}
