package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.SellRequest;
import com.example.mysterycard.dto.response.SellResponse;
import com.example.mysterycard.entity.Card;
import com.example.mysterycard.entity.ListSeller;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.ListSellerMapper;
import com.example.mysterycard.repository.CardRepo;
import com.example.mysterycard.repository.ListSellerRepo;
import com.example.mysterycard.service.CardService;
import com.example.mysterycard.service.ListSellerService;
import com.example.mysterycard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListSellerServiceImpl implements ListSellerService {
    @Autowired
    private ListSellerRepo listSellerRepo;
    @Autowired
    private ListSellerMapper listSellerMapper;
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private UserService userService;

    public SellResponse createListSeller(SellRequest request, UUID cardId) {
        Card card = cardRepo.findById(cardId).orElseThrow(() -> new AppException(ErrorCode.CARD_NOT_FOUND));
        ListSeller listSeller = listSellerMapper.toEntity(request);
        listSeller.setCard(card);
        card.getListSellers().add(listSeller);
        listSeller.setSeller(userService.getUser());
        ListSeller savedListSeller = listSellerRepo.save(listSeller);
        return listSellerMapper.toResponse(savedListSeller);
    }
    public List<SellResponse> getListSellersByCardId(UUID cardId) {
        Card card = cardRepo.findById(cardId).orElseThrow(() -> new AppException(ErrorCode.CARD_NOT_FOUND));
        List<ListSeller> listSellers = card.getListSellers();
        return listSellers.stream().map(listSellerMapper::toResponse).toList();
    }

}
