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
import com.example.mysterycard.mapper.CardMapper;
import com.example.mysterycard.mapper.OrderMapper;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.BlindBoxService;
import com.example.mysterycard.service.CategoryService;
import com.example.mysterycard.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
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
    private final OrderRepo orderRepo;
    private final OrderMapper orderMapper;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public BlindBoxResponse createBlindBox(BlindBoxRequest request) {
        BlindBox blindBox = blindBoxMapper.toBlindBox(request);
        List<RateConfig> rateConfigList = rateConfigRepo.findAll();
        blindBox.setRateConfigList(rateConfigList);
        for (RateConfig rc : rateConfigList) {
            rc.getBlindBoxesList().add(blindBox);
        }
        List<BlindBoxCard> boxCards = new ArrayList<>();
        List<UUID> listIds;
        if(request.getCategoryId()!=null){
            listIds = categoryService.getCardIdsByCategoryId(UUID.fromString(request.getCategoryId()));
        } else {
         listIds = request.getCardIds();
        }
        for(UUID cardId : listIds) {
            Card card = cardRepo.findById(cardId)
                    .orElseThrow(() -> new AppException(ErrorCode.CARD_NOT_FOUND));
            BlindBoxCard boxCard = new BlindBoxCard();
            boxCard.setBlindBox(blindBox);
            boxCard.setCard(card);
            boxCards.add(boxCard);
        }
        blindBox.setBlindBoxCards(boxCards);
        Map<Rarity, Double> groupProbs = calculateRarityProbabilities(blindBox, boxCards);
        Map<Rarity, Long> countPerRarity = boxCards.stream()
                .collect(Collectors.groupingBy(bc -> bc.getCard().getRarity(), Collectors.counting()));

        for (BlindBoxCard bc : boxCards) {
            Rarity rarity = bc.getCard().getRarity();
            double groupProb = groupProbs.getOrDefault(rarity, 0.0);
            long count = countPerRarity.getOrDefault(rarity, 1L);
            // Tỉ lệ của 1 thẻ = Tỉ lệ nhóm / số lượng thẻ trong nhóm đó
            bc.setRate(groupProb / count);
        }

        blindBox.setDrawPrice(calculateSmartEV(blindBox,boxCards));
        blindBox.setAllBoxPrice(calculateBlindBoxPriceFromList(boxCards));
        return blindBoxMapper.toBlindBoxResponse(blindBoxRepo.save(blindBox));
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
    @Transactional
    @Override
    public DrawResultResponse drawCard(UUID id) {
        // 1. Tìm purchase và LOCK BOX
        Order order = orderRepo.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        BlindBox box = blindBoxRepo.findByIdWithLock(order.getBlindBox().getBlindBoxId())
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));

        // 2. Lấy danh sách thẻ status = true (Dữ liệu tươi nhất từ DB)
        List<BlindBoxCard> activeCards = blindBoxCardRepo.findAllByBlindBoxAndStatusTrue(box);
        if (activeCards.isEmpty()) {
            box.setBlindBoxStatus(BlindBoxStatus.OUT_OF_STOCK);
            blindBoxRepo.save(box);
            throw new AppException(ErrorCode.EMPTY_BOX);
        }

        // 3. Chọn Rarity và Thẻ
        Map<Rarity, List<BlindBoxCard>> grouped = activeCards.stream()
                .collect(Collectors.groupingBy(bc -> bc.getCard().getRarity()));

        Rarity chosenRarity = chooseRarity(box, grouped);
        List<BlindBoxCard> candidates = new ArrayList<>(grouped.get(chosenRarity));
        Collections.shuffle(candidates);
        BlindBoxCard chosenCard = candidates.get(0);

        // 4. Đánh dấu thẻ đã mở (Chỉ lưu đúng 1 dòng này xuống DB)
        chosenCard.setStatus(false);
        blindBoxCardRepo.save(chosenCard);

        // 5. Cập nhật kinh tế Box dựa trên danh sách CÒN LẠI THỰC TẾ
        List<BlindBoxCard> remainingCards = activeCards.stream()
                .filter(c -> !c.getBlindBoxCardId().equals(chosenCard.getBlindBoxCardId()))
                .toList();

        if (remainingCards.isEmpty()) {
            box.setBlindBoxStatus(BlindBoxStatus.OUT_OF_STOCK);
            box.setDrawPrice(0L);
            box.setAllBoxPrice(0L);
        } else {
            // Sử dụng remainingCards cho cả 2 hàm tính giá
            box.setDrawPrice(calculateSmartEV(box, remainingCards));
            box.setAllBoxPrice(calculateBlindBoxPriceFromList(remainingCards));
        }
        blindBoxRepo.save(box);

        // 6. Lưu kết quả mở thưởng
        BlindBoxResult result = new BlindBoxResult();
        result.setCard(chosenCard.getCard());
        result.setOrder(order);
        result.setOwner(order.getBuyer());
        blindBoxCardResultRepo.save(result);

        // 7. Trả về Response
        DrawResultResponse drawResult = new DrawResultResponse();
        drawResult.setCard(cardMapper.toResponse(chosenCard.getCard()));
        drawResult.setDrawPrice(order.getTotalAmount()); // Giá lúc khách mua
        drawResult.setProfitOrLoss(chosenCard.getCard().getBasePrice() - order.getTotalAmount());

        return drawResult;
    }
    @Override
    public BlindBoxResponse getBlindBoxById(UUID id) {
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
    public List<BlindBoxCardResponse> getCardsInBlindBox(UUID blindBoxId) {
        BlindBox box = blindBoxRepo.findById(blindBoxId)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        return box.getBlindBoxCards().stream()
                .map(blindBoxCardMapper::toBlindBoxCardResponse)
                .toList();
    }
    @Override
    public OrderResponse buyBlindBox(UUID blindBoxId) {
        BlindBox box = blindBoxRepo.findById(blindBoxId)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        Users users = userService.getUser();
        Order order = new Order();
        order.setBlindBox(box);
        order.setBuyer(users);
        order.setTotalAmount(box.getDrawPrice());


        order = orderRepo.save(order);
        return orderMapper.toOrderResponse(order);
    }

    @Override
    public void deleteBlindBox(UUID id) {
        BlindBox box = blindBoxRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        blindBoxRepo.delete(box);
    }

    private Rarity chooseRarity(BlindBox box, Map<Rarity, List<BlindBoxCard>> grouped) {
        List<RateConfig> activeConfigs = box.getRateConfigList().stream()
                .filter(config -> grouped.containsKey(config.getCardRarity())
                        && !grouped.get(config.getCardRarity()).isEmpty())
                .toList();

        if (activeConfigs.isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_BOX);
        }

        // 2. Tính tổng dropRate của các nhóm còn lại (để chuẩn hóa về 100%)
        double currentTotalWeight = activeConfigs.stream()
                .mapToDouble(RateConfig::getDropRate)
                .sum();

        // 3. Random giá trị trong khoảng tổng trọng số mới
        double randomValue = Math.random() * currentTotalWeight;
        double cumulative = 0.0;

        for (RateConfig config : activeConfigs) {
            cumulative += config.getDropRate();
            if (randomValue <= cumulative) {
                return config.getCardRarity();
            }
        }

        return activeConfigs.get(0).getCardRarity();
    }

    @Override
    public BlindBoxProbabilitiesResponse getProbabilities(UUID blindBoxId) {
        BlindBox box = blindBoxRepo.findById(blindBoxId)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        List<BlindBoxCard> activeCards = blindBoxCardRepo.findAllByBlindBoxAndStatusTrue(box);

        Map<Rarity, Double> probabilities = calculateRarityProbabilities(box,activeCards);

        List<BlindBoxProbabilitiesResponse.ProbabilityItem> items = probabilities.entrySet().stream()
                .map(e -> new BlindBoxProbabilitiesResponse.ProbabilityItem(e.getKey(), e.getValue()))
                .toList();

        return new BlindBoxProbabilitiesResponse(blindBoxId, items);
    }
    private Map<Rarity, Double> calculateRarityProbabilities(BlindBox box, List<BlindBoxCard> activeCards) {
        // 1. Xác định các nhóm Rarity nào thực sự còn thẻ (Status = true)
        // Việc dùng Set giúp tra cứu nhanh hơn O(1)
        Set<Rarity> activeRarities = activeCards.stream()
                .map(bc -> bc.getCard().getRarity())
                .collect(Collectors.toSet());

        Map<Rarity, Double> probabilities = new HashMap<>();

        // 2. Tính tổng trọng số (DropRate) CHỈ của những nhóm còn thẻ
        double totalActiveWeight = box.getRateConfigList().stream()
                .filter(config -> activeRarities.contains(config.getCardRarity()))
                .mapToDouble(RateConfig::getDropRate)
                .sum();

        // 3. Phân bổ lại tỷ lệ % dựa trên tổng trọng số mới
        for (RateConfig config : box.getRateConfigList()) {
            Rarity rarity = config.getCardRarity();
            if (activeRarities.contains(rarity)) {
                // Tỷ lệ mới = (Trọng số gốc / Tổng trọng số còn lại) * 100
                double probability = (totalActiveWeight > 0)
                        ? (config.getDropRate() / totalActiveWeight) * 100
                        : 0.0;
                probabilities.put(rarity, probability);
            } else {
                // Nhóm đã hết thẻ thì tỷ lệ bằng 0
                probabilities.put(rarity, 0.0);
            }
        }

        return probabilities;
    }
    private Long calculateSmartEV(BlindBox box, List<BlindBoxCard> activeCards) {
        int currentCount = activeCards.size();
        int initialCount = box.getBlindBoxCards().size();
        double fillRate = (double) currentCount / initialCount;

        // 1. CHIẾN LƯỢC CHỐT HẠ (End-game)
        if (currentCount <= (initialCount * 0.15) || currentCount < 10) {
            double totalValue = activeCards.stream()
                    .mapToDouble(c -> c.getCard().getBasePrice()).sum();
            return Math.round((totalValue / currentCount) * 1.05); // Cộng 5% phí sàn
        }

        // Group cards theo Rarity trước để tránh stream nhiều lần trong vòng lặp
        Map<Rarity, List<BlindBoxCard>> cardsByRarity = activeCards.stream()
                .collect(Collectors.groupingBy(c -> c.getCard().getRarity()));

        double totalEV = 0.0;

        for (Rarity rarity : Rarity.values()) {
            List<BlindBoxCard> cards = cardsByRarity.getOrDefault(rarity, Collections.emptyList());
            if (cards.isEmpty()) continue;

            double currentProb = (double) cards.size() / currentCount;
            double avgPrice = cards.stream().mapToDouble(c -> c.getCard().getBasePrice()).average().orElse(0.0);

            // 2. HỆ SỐ NHÂN MỀM (Dynamic Multipliers)
            double multiplier = calculateMultiplier(rarity, fillRate);

            totalEV += currentProb * avgPrice * multiplier;
        }

        // 3. GIÁ SÀN BẢO VỆ (Floor Price)
        // Đảm bảo giá Box không thấp hơn trung bình thẻ Common/Uncommon hiện có
        double floorPrice = activeCards.stream()
                .filter(c -> c.getCard().getRarity() == Rarity.COMMON || c.getCard().getRarity() == Rarity.UNCOMMON)
                .mapToDouble(c -> c.getCard().getBasePrice()).average().orElse(3000.0);

        totalEV = Math.max(totalEV, floorPrice);

        return Math.round(totalEV * 1.05);
    }

    // Hàm hỗ trợ tính Multiplier mượt mà
    private double calculateMultiplier(Rarity rarity, double fillRate) {
        return switch (rarity) {
            case COMMON, UNCOMMON -> 1.0;
            case RARE -> fillRate > 0.8 ? 0.4 : 1.2;
            case SUPER_RARE -> fillRate > 0.7 ? 0.0 : 1.5;
            case ULTRA_RARE -> fillRate > 0.5 ? 0.0 : 2.5;
            case SECRET_RARE -> fillRate > 0.3 ? 0.0 : 5.0;
            default -> 1.0;
        };
    }

    public Long calculateBlindBoxPriceFromList(List<BlindBoxCard> activeCards) {
        double totalValue = activeCards.stream()
                .mapToDouble(bc -> bc.getCard().getBasePrice())
                .sum();

        return Math.round(totalValue * 1.03); // Giữ nguyên insurance 3% của bạn
    }


}
