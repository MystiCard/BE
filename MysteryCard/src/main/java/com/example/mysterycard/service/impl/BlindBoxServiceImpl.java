package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.BlindBoxRequest;
import com.example.mysterycard.dto.response.BlindBoxCardResponse;
import com.example.mysterycard.dto.response.BlindBoxResponse;
import com.example.mysterycard.dto.response.CardResponse;
import com.example.mysterycard.dto.response.RateConfigResponse;
import com.example.mysterycard.entity.BlindBox;
import com.example.mysterycard.entity.BlindBoxCard;
import com.example.mysterycard.entity.Card;
import com.example.mysterycard.entity.RateConfig;
import com.example.mysterycard.enums.Rarity;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.BlindBoxCardMapper;
import com.example.mysterycard.mapper.BlindBoxMapper;
import com.example.mysterycard.mapper.CardMapper;
import com.example.mysterycard.repository.BlindBoxCardRepo;
import com.example.mysterycard.repository.BlindBoxRepo;
import com.example.mysterycard.repository.CardRepo;
import com.example.mysterycard.repository.RateConfigRepo;
import com.example.mysterycard.service.BlindBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class BlindBoxServiceImpl implements BlindBoxService {
    @Autowired
    private BlindBoxRepo blindBoxRepo;
    @Autowired
    private RateConfigRepo rateConfigRepo;
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private BlindBoxMapper blindBoxMapper;
    @Autowired
    private BlindBoxCardRepo blindBoxCardRepo;
    @Autowired
    private BlindBoxCardMapper blindBoxCardMapper;

    @Autowired
    private CardMapper cardMapper;
    @Override
    public BlindBoxResponse createBlindBox(BlindBoxRequest request) {
        BlindBox blindBox = blindBoxMapper.toBlindBox(request);
        List<RateConfig> rateConfigList = rateConfigRepo.findAll();
        blindBox.setRateConfigList(rateConfigList);
        for (RateConfig rc : rateConfigList) {
            rc.getBlindBoxesList().add(blindBox);
        }
        List<BlindBoxCard> boxCards = new ArrayList<>();
        for(UUID cardId : request.getCardIds()) {
            Card card = cardRepo.findById(cardId).orElse(null);
            BlindBoxCard boxCard = new BlindBoxCard();
            boxCard.setBlindBox(blindBox);
            boxCard.setCard(card);
            boxCards.add(boxCard);
        }
        blindBox.setBlindBoxCards(boxCards);
        blindBox = blindBoxRepo.save(blindBox);
        double totalWeight = calculateTotalWeight(blindBox);
        for(BlindBoxCard bc : blindBox.getBlindBoxCards()) {
            double weight = getWeight(bc, blindBox);
            double rate = (weight / totalWeight) * 100;
            bc.setRate(rate);
        }
        blindBox = blindBoxRepo.save(blindBox);
        return blindBoxMapper.toBlindBoxResponse(blindBox);
    }

    private double getWeight(BlindBoxCard bc, BlindBox box){
        Rarity rarity = bc.getCard().getRarity();
        RateConfig config = box.getRateConfigList().stream()
                .filter(rc -> rc.getCardRarity() == rarity)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.RATE_CONFIG_NOT_FOUND));
        return config.getDropRate();

    }
    public double calculateTotalWeight(BlindBox box) {
        return box.getBlindBoxCards().stream()
                .filter(BlindBoxCard::isStatus)
                .mapToDouble(bc -> getWeight(bc, box))
                .sum();
    }
    public CardResponse drawCard(Long id) {
        BlindBox box = blindBoxRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        double totalWeight = calculateTotalWeight(box);
        if (totalWeight <= 0) {
            throw new AppException(ErrorCode.DRAW_CARD_FAILED); // không còn thẻ nào để rút }
        }
        double randomValue = Math.random() * totalWeight;

        double cumulativeWeight = 0.0;
        List<BlindBoxCard> activeCards = box.getBlindBoxCards().stream()
                .filter(BlindBoxCard::isStatus) .toList();
        for (BlindBoxCard bc : activeCards) {
            cumulativeWeight += getWeight(bc, box);
            if (randomValue <= cumulativeWeight) {
                bc.setStatus(false);
                blindBoxCardRepo.save(bc);
                updateRates(box);
                return cardMapper.toResponse(bc.getCard());
            }
        }
        throw new AppException(ErrorCode.DRAW_CARD_FAILED);
    }
    private void updateRates(BlindBox box) {
        double totalWeight = calculateTotalWeight(box);
        if (totalWeight <= 0) return;

        List<BlindBoxCard> activeCards = box.getBlindBoxCards().stream()
                .filter(BlindBoxCard::isStatus)
                .toList();

        for (BlindBoxCard bc : activeCards) {
            double weight = getWeight(bc, box);
            double rate = (weight / totalWeight) * 100;
            bc.setRate(rate);

        }
        blindBoxCardRepo.saveAll(activeCards);
    }

    @Override
    public BlindBoxResponse getBlindBoxById(Long id) {
        BlindBox box = blindBoxRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        return blindBoxMapper.toBlindBoxResponse(box);
    }

    @Override
    public List<BlindBoxResponse> getAllBlindBoxes() {
        List<BlindBox> boxes = blindBoxRepo.findAll();
        return boxes.stream()
                .map(blindBoxMapper::toBlindBoxResponse)
                .toList();
    }

    @Override
    public List<BlindBoxCardResponse> getCardsInBlindBox(Long blindBoxId) {
        BlindBox box = blindBoxRepo.findById(blindBoxId)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        return box.getBlindBoxCards().stream()
                .map(blindBoxCardMapper::toBlindBoxCardResponse)
                .toList();
    }


}
