package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.CartRequest;
import com.example.mysterycard.dto.response.CartResponse;
import com.example.mysterycard.entity.Cart;
import com.example.mysterycard.entity.ListSeller;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.CardMapper;
import com.example.mysterycard.mapper.CartMapper;
import com.example.mysterycard.repository.CartRepo;
import com.example.mysterycard.repository.ListSellerRepo;
import com.example.mysterycard.service.CartService;
import com.example.mysterycard.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final ListSellerRepo listSellerRepo;
    private final CartMapper cartMapper;
    private final CartRepo cartRepo;
    private final UserService userService;

    @Override
    public CartResponse addToCard(CartRequest request) {
        log.info("0");
     ListSeller listSeller = listSellerRepo.findByListSellerId(request.getListSellerId());
     log.info("list seeller {}", listSeller);
     if(listSeller == null)
     {
         throw  new AppException(ErrorCode.LIST_SELLER_NOT_FOUND);
     }
    log.info("1");
    if(request.getQuantity() > listSeller.getQuantity())
    {
        throw  new AppException(ErrorCode.QUANTITY_OVER_AVAIABLE);
    }
        Users users = userService.getUser();
        log.info("2");
    Cart cart = Cart.builder()
            .user(users)
            .listSeller(listSeller)
            .price(listSeller.getPrice())
            .quantity(request.getQuantity())
            .build();
        log.info("3");
        return cartMapper.toCartResponse(cartRepo.save(cart));
    }

    @Override
    public CartResponse update(CartRequest request,UUID cartId) {
        Cart cart = cartRepo.findById(cartId).orElseThrow(
                ()-> new AppException(ErrorCode.CART_NOT_FOUND)
        );
        ListSeller listSeller = listSellerRepo.findById(request.getListSellerId()).orElseThrow(
                ()-> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND)
        );
        cart.setListSeller(listSeller);
        cart.setPrice(listSeller.getPrice());
        cart.setQuantity(request.getQuantity());

        return cartMapper.toCartResponse(cartRepo.save(cart));
    }

    @Override
    public void remove(UUID cartId) {
        Cart cart = cartRepo.findById(cartId).orElseThrow(
                ()-> new AppException(ErrorCode.CART_NOT_FOUND)
        );
        cartRepo.delete(cart);
    }

    @Override
    public Page<CartResponse> getALl(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());

        return cartRepo.findAll(pageable).map(cartMapper::toCartResponse);
    }


}
