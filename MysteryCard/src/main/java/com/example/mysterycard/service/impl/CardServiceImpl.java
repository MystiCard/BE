package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.CardRequest;
import com.example.mysterycard.dto.response.CardResponse;
import com.example.mysterycard.entity.Card;
import com.example.mysterycard.entity.Category;
import com.example.mysterycard.entity.Image;
import com.example.mysterycard.entity.RateConfig;
import com.example.mysterycard.enums.Rarity;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.CardMapper;
import com.example.mysterycard.repository.CardRepo;
import com.example.mysterycard.repository.CategoryRepo;
import com.example.mysterycard.repository.RateConfigRepo;
import com.example.mysterycard.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CardServiceImpl implements CardService {
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private RateConfigRepo rateConfigRepo;

    @Autowired
    private CardMapper cardMapper;
    @Override
    public CardResponse getCardById(UUID id) {
        Card card = cardRepo.findById(id).orElseThrow(()-> new AppException(ErrorCode.CARD_NOT_FOUND));
        return cardMapper.toResponse(card);
    }

    @Override
    public List<CardResponse> getAllCards() {
        return cardRepo.findAll().stream().map(cardMapper::toResponse).toList();
    }

    @Override
    public void deleteCardById(UUID id) {
        Card card = cardRepo.findById(id).orElseThrow(()-> new AppException(ErrorCode.CARD_NOT_FOUND));
        cardRepo.delete(card);
    }

    @Override
    public CardResponse createCard(CardRequest request) {
        if(cardRepo.existsCardByName(request.getName())){
            throw new AppException(ErrorCode.CARD_DUPLICATE);
        }
        Card card = cardMapper.toCard(request);
        UUID cateId = UUID.fromString(request.getCategoryId());
        Category category = categoryRepo.findById(cateId).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        category.addCard(card);
        if (request.getImageUrl() != null) {
            Image img = new Image();
            img.setImageUrl(request.getImageUrl());
            img.setCard(card);
            card.getImages().add(img);
        }
        card = cardRepo.save(card);
        setMinMaxPrice(card);
        return cardMapper.toResponse(card);
    }

    @Override
    public CardResponse updateCard(UUID id, CardRequest request) {
        Card card = cardRepo.findById(id).orElseThrow(()-> new AppException(ErrorCode.CARD_NOT_FOUND));
        card.setName(request.getName());
        card.setRarity(request.getRarity());
//        card.setImageUrl(request.getImageUrl());
        card = cardRepo.save(card);
        return cardMapper.toResponse(card);
    }

    public void setMinMaxPrice(Card card) {
        Rarity rarity = card.getRarity();
        RateConfig config = rateConfigRepo.findAll().stream()
                .filter(rc -> rc.getCardRarity() == rarity)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.RATE_CONFIG_NOT_FOUND));
        card.setMinPrice(card.getBasePrice()-card.getBasePrice()*config.getVariancePercent());
        card.setMaxPrice(card.getBasePrice()+card.getBasePrice()*config.getVariancePercent());
        cardRepo.save(card);
    }
}
