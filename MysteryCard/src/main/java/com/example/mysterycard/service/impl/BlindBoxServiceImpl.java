package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.BlindBoxRequest;
import com.example.mysterycard.dto.response.*;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.BlindBoxStatus;
import com.example.mysterycard.enums.Rarity;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.BlindBoxCardMapper;
import com.example.mysterycard.mapper.BlindBoxMapper;
import com.example.mysterycard.mapper.BlindBoxPurChaseMapper;
import com.example.mysterycard.mapper.CardMapper;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.BlindBoxService;
import com.example.mysterycard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BlindBoxServiceImpl implements BlindBoxService {
    private final BlindBoxRepo blindBoxRepo;
    private final RateConfigRepo rateConfigRepo;
    private final CardRepo cardRepo;
    private final BlindBoxMapper blindBoxMapper;
    private final BlindBoxCardRepo blindBoxCardRepo;
    private final BlindBoxCardResultRepo blindBoxCardResultRepo;
    private final BlindBoxCardMapper blindBoxCardMapper;
    private final CardMapper cardMapper;
    private final UserService userService;
    private final BlindBoxPurChaseRepo blindBoxPurChaseRepo;
    private final BlindBoxPurChaseMapper blindBoxPurChaseMapper;
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
        double totalWeight = calculateTotalWeight(blindBox);
        for(BlindBoxCard bc : blindBox.getBlindBoxCards()) {
            double weight = getWeight(bc, blindBox);
            double rate = (weight / totalWeight) * 100;
            bc.setRate(rate);
        }
        blindBox.setDrawPrice(calculateEVWithRatio(blindBox));
        blindBox.setAllBoxPrice(calculateBlindBoxPrice(blindBox));
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
    @Override
    public DrawResultResponse drawCard(UUID id) {
        BlindBoxPurChase purchase = blindBoxPurChaseRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_PURCHASE_NOT_FOUND));
        BlindBox box = purchase.getBlindBox();
        List<BlindBoxCard> activeCards = box.getBlindBoxCards().stream()
                .filter(BlindBoxCard::isStatus) .toList();
        Map<Rarity, List<BlindBoxCard>> grouped = activeCards.stream()
                .collect(Collectors.groupingBy(bc -> bc.getCard().getRarity()));
        Rarity chosenRarity = chooseRarity(box, grouped);
        System.out.println("Chosen Rarity: " + chosenRarity.name());
        List<BlindBoxCard> candidates = grouped.getOrDefault(chosenRarity, List.of());
        if (candidates.isEmpty()) {
            throw new AppException(ErrorCode.DRAW_CARD_FAILED);
        }
        BlindBoxCard chosenCard = candidates.get(new Random().nextInt(candidates.size()));
        chosenCard.setStatus(false);
        blindBoxCardRepo.save(chosenCard);

        CardResponse cardResponse =  cardMapper.toResponse(chosenCard.getCard());
        DrawResultResponse drawResult = new DrawResultResponse();
        drawResult.setCard(cardResponse);
        drawResult.setDrawPrice(box.getDrawPrice());
        double profitOrLoss =  chosenCard.getCard().getBasePrice() - box.getDrawPrice();
        drawResult.setProfitOrLoss(profitOrLoss);

        updateRates(box);
        box.setDrawPrice(calculateEVWithRatio(box));
        box.setAllBoxPrice(calculateBlindBoxPrice(box));
        blindBoxRepo.save(box);
        BlindBoxResult result = new BlindBoxResult();
        result.setCard(chosenCard.getCard());
        result.setBlindBoxPurchase(purchase);
        result.setOwner(purchase.getBuyer());
        blindBoxCardResultRepo.save(result);
//        Danh dau da mo
//        purchase.setOpened(true);
//        blindBoxPurChaseRepo.save(purchase);


        return drawResult;
    }
    private void updateRates(BlindBox box) {
        double totalWeight = calculateTotalWeight(box);
        if (totalWeight <= 0)
            {   box.setBlindBoxStatus(BlindBoxStatus.OUT_OF_STOCK);
                blindBoxRepo.save(box);
                return;}

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
    @Override
    public BlindBoxPurChaseResponse buyBlindBox(Long blindBoxId) {
        BlindBox box = blindBoxRepo.findById(blindBoxId)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        Users users = userService.getUser();
        BlindBoxPurChase purchase = new BlindBoxPurChase();
        purchase.setBlindBox(box);
        purchase.setBuyer(users);
        purchase.setPrice(box.getDrawPrice());


        purchase = blindBoxPurChaseRepo.save(purchase);
        return blindBoxPurChaseMapper.toResponse(purchase);
    }

    @Override
    public void deleteBlindBox(Long id) {
        BlindBox box = blindBoxRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        blindBoxRepo.delete(box);
    }

    private Rarity chooseRarity(BlindBox box, Map<Rarity, List<BlindBoxCard>> grouped) {
        double totalWeight = 0.0;
        Map<Rarity, Double> rarityWeights = new HashMap<>();

        // Lấy trọng số từ RateConfig cho từng loại hiếm
        for (RateConfig config : box.getRateConfigList()) {
            Rarity rarity = config.getCardRarity();
            long count = grouped.getOrDefault(rarity, List.of()).size();
            double weight = config.getDropRate() * count; // dropRate * số thẻ còn lại
            rarityWeights.put(rarity, weight);
            totalWeight += weight;
        }

        if (totalWeight <= 0) {
            throw new AppException(ErrorCode.DRAW_CARD_FAILED);
        }

        // Random chọn loại hiếm
        double randomValue = Math.random() * totalWeight;
        double cumulative = 0.0;

        for (Map.Entry<Rarity, Double> entry : rarityWeights.entrySet()) {
            cumulative += entry.getValue();
            if (randomValue <= cumulative) {
                return entry.getKey();
            }
        }

        throw new AppException(ErrorCode.DRAW_CARD_FAILED);
    }

    @Override
    public BlindBoxProbabilitiesResponse getProbabilities(Long blindBoxId) {
        BlindBox box = blindBoxRepo.findById(blindBoxId)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));

        Map<Rarity, Double> probabilities = calculateRarityProbabilities(box);

        List<BlindBoxProbabilitiesResponse.ProbabilityItem> items = probabilities.entrySet().stream()
                .map(e -> new BlindBoxProbabilitiesResponse.ProbabilityItem(e.getKey(), e.getValue()))
                .toList();

        return new BlindBoxProbabilitiesResponse(blindBoxId, items);
    }
    private Map<Rarity, Double> calculateRarityProbabilities(BlindBox box) {
        Map<Rarity, Double> rarityWeights = new HashMap<>();
        double totalWeight = 0.0;

        Map<Rarity, List<BlindBoxCard>> grouped = box.getBlindBoxCards().stream()
                .filter(BlindBoxCard::isStatus)
                .collect(Collectors.groupingBy(bc -> bc.getCard().getRarity()));

        for (RateConfig config : box.getRateConfigList()) {
            Rarity rarity = config.getCardRarity();
            long count = grouped.getOrDefault(rarity, List.of()).size();
            double weight = config.getDropRate() * count;
            rarityWeights.put(rarity, weight);
            totalWeight += weight;
        }

        Map<Rarity, Double> probabilities = new HashMap<>();
        for (Map.Entry<Rarity, Double> entry : rarityWeights.entrySet()) {
            double probability = (totalWeight > 0) ? (entry.getValue() / totalWeight) * 100 : 0.0;
            probabilities.put(entry.getKey(), probability);
        }

        return probabilities;
    }
    private double calculateEVWithRatio(BlindBox box) {
        Map<Rarity, Double> probabilities = calculateRarityProbabilities(box);
        double ev = 0.0;

        for (Map.Entry<Rarity, Double> entry : probabilities.entrySet()) {
            Rarity rarity = entry.getKey();
            double ratio = entry.getValue(); // %

            // Ngưỡng ratio cho từng loại
            double threshold = switch (rarity) {
                case RARE -> 5.0;        // Rare chỉ cộng nếu ≥ 10%
                case SUPER_RARE -> 5.0;   // SR chỉ cộng nếu ≥ 5%
                case SECRET_RARE -> 10.0; // SC ≥ 10%
                default -> 0.0;           // Common, Uncommon luôn cộng
            };

            if (ratio >= threshold) {
                double probability = ratio / 100.0;

                List<BlindBoxCard> cards = box.getBlindBoxCards().stream()
                        .filter(BlindBoxCard::isStatus)
                        .filter(bc -> bc.getCard().getRarity() == rarity)
                        .toList();

                if (!cards.isEmpty()) {
                    double avgValue = cards.stream()
                            .mapToDouble(bc -> bc.getCard().getBasePrice()) // giả sử Card có field value
                            .average()
                            .orElse(0.0);

                    ev += probability * avgValue;
                }
            }
        }

        return ev;
    }

    public double calculateBlindBoxPrice(BlindBox box) {
        double totalValue = box.getBlindBoxCards().stream()
                .filter(BlindBoxCard::isStatus)
                .mapToDouble(bc -> bc.getCard().getBasePrice())
                .sum();

        double insuranceFactor = 1.03;
        double finalPrice = totalValue * insuranceFactor;

        return finalPrice;
    }

}
