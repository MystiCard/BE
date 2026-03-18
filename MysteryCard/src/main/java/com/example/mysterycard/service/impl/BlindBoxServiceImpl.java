package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.BlindBoxRequest;
import com.example.mysterycard.dto.request.transaction.TransactionRequest;
import com.example.mysterycard.dto.response.*;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.BlindBoxStatus;
import com.example.mysterycard.enums.Rarity;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.enums.TransactionType;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.*;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.BlindBoxService;
import com.example.mysterycard.service.CategoryService;
import com.example.mysterycard.service.TransactionService;
import com.example.mysterycard.service.UserService;
import com.example.mysterycard.specification.ShipmentSpecification;
import com.example.mysterycard.utils.CloudiaryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    private final CategoryService categoryService;
    private final TransactionService transactionService;
    private final BlindBoxResultMapper blindBoxResultMapper;
    private final ShipmentMapper shipmentMapper;
    private final CloudiaryUtils cloudiaryUtils;
    private final OrderRepo orderRepo;
    private final ShipmentRepo shipmentRepo;

    @Override
    @Transactional
    public BlindBoxResponse createBlindBox(BlindBoxRequest request, MultipartFile file) {
        BlindBox blindBox = blindBoxMapper.toBlindBox(request);
        if (file != null) {
            blindBox.setImageUrl(cloudiaryUtils.uploadImage(file));
        }
        List<RateConfig> rateConfigList = rateConfigRepo.findAll();
        blindBox.setRateConfigList(rateConfigList);
        for (RateConfig rc : rateConfigList) {
            rc.getBlindBoxesList().add(blindBox);
        }
        List<BlindBoxCard> boxCards = new ArrayList<>();
        List<UUID> listIds;
        if (request.getCategoryId() != null) {
            listIds = categoryService.getCardIdsByCategoryId(UUID.fromString(request.getCategoryId()));
        } else {
            listIds = request.getCardIds();
        }
        for (UUID cardId : listIds) {
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

        blindBox.setDrawPrice(calculateSmartEV(blindBox, boxCards));
        blindBox.setAllBoxPrice(calculateBlindBoxPriceFromList(boxCards));
        return blindBoxMapper.toBlindBoxResponse(blindBoxRepo.save(blindBox));
    }

    @Transactional
    @Override
    public DrawResultResponse drawCard(UUID id) {
        // 1. Tìm purchase và LOCK BOX

        BlindBox box = blindBoxRepo.findByIdWithLock(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        double drawPrice = box.getDrawPrice();
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
        Users owner = userService.getUser();
        result.setOwner(owner);
        result.setBlindBox(box);
        blindBoxCardResultRepo.save(result);

        // 7. Trả về Response
        DrawResultResponse drawResult = DrawResultResponse.builder()
                .card(cardMapper.toResponse(chosenCard.getCard()))
                .drawPrice(drawPrice)
                .profitOrLoss(chosenCard.getCard().getBasePrice() - drawPrice)
                .build();
        return drawResult;
    }

    @Override
    public BlindBoxResponse getBlindBoxById(UUID id) {
        BlindBox box = blindBoxRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        return blindBoxMapper.toBlindBoxResponse(box);
    }

    @Override
    public Page<BlindBoxResponse> getAllBlindBoxes(int page, int size, BlindBoxStatus blindBoxStatus) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<BlindBox> boxes = null;
        if (blindBoxStatus != null) {
            boxes = blindBoxRepo.findAllByBlindBoxStatus(blindBoxStatus, pageable);
        } else {
            boxes = blindBoxRepo.findAll(pageable);
        }
        return boxes.map(blindBoxMapper::toBlindBoxResponse);
    }

    @Override
    public Page<BlindBoxCardResponse> getCardsInBlindBox(UUID blindBoxId, int size, int page) {
        BlindBox box = blindBoxRepo.findById(blindBoxId)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<BlindBoxCard> cards = blindBoxCardRepo.findByBlindBox_blindBoxId(box.getBlindBoxId(), pageable);
        return cards.map(blindBoxCardMapper::toBlindBoxCardResponse);


    }

    @Transactional
    @Override
    public List<DrawResultResponse> buyBlindBox(UUID blindBoxId, boolean buyAll) {
        List<DrawResultResponse> drawResultResponseList  = new ArrayList<>();
        BlindBox box = blindBoxRepo.findById(blindBoxId)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));


        if(!buyAll){
            transactionService.createTransaction(TransactionRequest.builder()
                    .transactionType(TransactionType.PAYMENT)
                    .drawPrice(box.getDrawPrice())
                    .build());
            DrawResultResponse result = drawCard(box.getBlindBoxId());
            drawResultResponseList.add(result);

        }
        else {
            transactionService.createTransaction(TransactionRequest.builder()
                    .transactionType(TransactionType.PAYMENT)
                    .drawPrice(box.getAllBoxPrice())
                    .build());
            List<BlindBoxCard> cards  = blindBoxCardRepo.findAllByBlindBoxAndStatusTrue(box);
            for(BlindBoxCard card : cards){
                DrawResultResponse results = DrawResultResponse.builder()
                        .card(cardMapper.toResponse(card.getCard()))
                        .build();
                card.setStatus(false);
                blindBoxCardRepo.save(card);
                drawResultResponseList.add(results);
                BlindBoxResult blindBoxResult = new BlindBoxResult();
                blindBoxResult.setStatus(BlindBoxResult.ResultStatus.NOT_RECEIVED);
                blindBoxResult.setOwner(userService.getUser());
                blindBoxResult.setCard(card.getCard());
                blindBoxResult.setOpenedAt(LocalDateTime.now());
                blindBoxResult.setBlindBox(box);
                blindBoxCardResultRepo.save(blindBoxResult);
            }
            box.setBlindBoxStatus(BlindBoxStatus.OUT_OF_STOCK);
            box.setDrawPrice(0);
            box.setAllBoxPrice(0l);
            blindBoxRepo.save(box);

        }
        return drawResultResponseList;
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

        Map<Rarity, Double> probabilities = calculateRarityProbabilities(box, activeCards);

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
        if (currentCount <= (initialCount * 0.5) || currentCount < 10) {
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
            case RARE -> fillRate > 0.7 ? 0.4 : 1.1;
            case SUPER_RARE -> fillRate > 0.4 ? 0.0 : 1.25;
            case ULTRA_RARE -> fillRate > 0.25 ? 0.0 : 2;
            case SECRET_RARE -> fillRate > 0.2 ? 0.0 : 3;
            default -> 1.0;
        };
    }

    public Long calculateBlindBoxPriceFromList(List<BlindBoxCard> activeCards) {
        double totalValue = activeCards.stream()
                .mapToDouble(bc -> bc.getCard().getBasePrice())
                .sum();

        return Math.round(totalValue * 1.03); // Giữ nguyên insurance 3% của bạn
    }

    public Page<BlindBoxResultResponse> getAllResultsForUser(int page, int size, UUID blindBoxId, BlindBoxResult.ResultStatus status) {
        BlindBox box = blindBoxRepo.findById(blindBoxId)
                .orElseThrow(() -> new AppException(ErrorCode.BLIND_BOX_NOT_FOUND));
        Users user = userService.getUser();
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<BlindBoxResult> resultsPage = null;
        if (status == null) {
            resultsPage = blindBoxCardResultRepo.findByOwner_UserIdAndBlindBox_BlindBoxId(user.getUserId(), box.getBlindBoxId(), pageable);

        } else {
            resultsPage = blindBoxCardResultRepo.findByOwner_UserIdAndBlindBox_BlindBoxIdAndStatus(user.getUserId(), box.getBlindBoxId(), status, pageable);

        }


        return resultsPage.map(result -> {
            BlindBoxResultResponse response = BlindBoxResultResponse.builder()
                    .blindBoxResultId(result.getBlindBoxResultId())
                    .cardName(result.getCard().getName())
                    .cardImageUrl(result.getCard().getImages().isEmpty() ? null : result.getCard().getImages().get(0).getImageUrl())
                    .rarity(result.getCard().getRarity().toString())
                    .openedAt(result.getOpenedAt())
                    .blindBoxName(result.getBlindBox().getName())
                    .status(result.getStatus())
                    .cardPrice(result.getCard().getBasePrice())
                    .build();
            return response;
        });
    }

    public Page<BlindBoxOpenResponse> getAllOpenedBlindBoxByUser(int page, int size) {
        Users user = userService.getUser();
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<BlindBox> resultsPage = blindBoxCardResultRepo.findDistinctBlindBoxByOwner_UserId(user.getUserId(), pageable);

        return resultsPage.map(result -> {
            BlindBoxOpenResponse response = BlindBoxOpenResponse.builder()
                    .blindBoxId(result.getBlindBoxId())
                    .name(result.getName())
                    .imageUrl(result.getImageUrl())
                    .build();
            return response;
        });
    }

    public BlindBoxResultResponse toBlindBoxResultResponse(BlindBoxResult result) {
        Card card = result.getCard();
        String imageUrl = card.getImages().isEmpty() ? null : card.getImages().get(0).getImageUrl();

        return BlindBoxResultResponse.builder()
                .blindBoxResultId(result.getBlindBoxResultId())
                .openedAt(result.getOpenedAt())
                .cardName(card.getName())
                .cardImageUrl(imageUrl)
                .rarity(card.getRarity().name())
                .blindBoxName(result.getBlindBox().getName()) // giả sử BlindBox có field name
                .build();
    }

    @Override
    public Page<BlindBoxResultResponse> getALlResultsOpened(int page, int size, BlindBoxResult.ResultStatus resultStatus) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("openedAt").descending());
        Users user = userService.getUser();

        Page<BlindBoxResult> results = null;
        if (resultStatus != null) {
            results = blindBoxCardResultRepo.findByOwner_UserIdAndStatus(user.getUserId(), resultStatus, pageable);
        } else {
            results = blindBoxCardResultRepo.findByOwner_UserId(user.getUserId(), pageable);
        }

        return results.map(result -> {
            BlindBoxResultResponse response = BlindBoxResultResponse.builder()
                    .blindBoxResultId(result.getBlindBoxResultId())
                    .cardName(result.getCard().getName())
                    .cardImageUrl(result.getCard().getImages().isEmpty() ? null : result.getCard().getImages().get(0).getImageUrl())
                    .rarity(result.getCard().getRarity().toString())
                    .openedAt(result.getOpenedAt())
                    .blindBoxName(result.getBlindBox().getName())
                    .status(result.getStatus())
                    .cardPrice(result.getCard().getBasePrice())
                    .build();

            return response;
        });

    }

    @Override
    public PageResponse<BlindBoxShipmentResponse> getBlindBoxShipment(int page, int size, ShippingStatus status) {
        Users user = userService.getUser();
        List<Order> orders = orderRepo.getOrderHaveShipCard(user);
       log.info("Orders {}",orders.size());
        List<BlindBoxShipmentResponse> list = new ArrayList<>();
        if (orders != null && orders.size() > 0) {

            for(Order o : orders)
            {
                   log.info("Order {}",o.getOrderId());
                if(o.getShipmentList() != null && o.getShipmentList().size() > 0 && o.getBlindBoxResults() != null)
                {
//                    Specification<Shipment> specification = Specification.allOf(
//                            ShipmentSpecification.findByStatus(status),
//                            ShipmentSpecification.findByOrder(o)
//                    );
                    List<Shipment> shipmentsList = o.getShipmentList();
                    log.info("Shipment list {}", shipmentsList.size());
                    for (Shipment s : shipmentsList) {

                        List<BlindBoxShipmentResponse.BlinboxShipDetail> blinboxShipDetails = new ArrayList<>();
                        log.info("Shipment ID {}", s.getShipmentId());
                        log.info("1 ");
                        if((status != null && s.getShipmentStatus().equals(status))  || status == null)
                        {
                            BlindBoxShipmentResponse blr = BlindBoxShipmentResponse.builder()
                                    .orderId(o.getOrderId())
                                    .build();
                            if(s.getBlindBoxResults() != null )
                            {

                                log.info("2 ");
                                blr.setShipmentResponse(shipmentMapper.entityToResponse(s));
                                for(BlindBoxResult br : s.getBlindBoxResults())
                                {
                                    log.info("3 ");
                                    blinboxShipDetails.add(blindBoxResultMapper.entityToResponse(br));
                                }
                            }
                            blr.setBlinboxShipDetail(blinboxShipDetails);
                            list.add(blr);
                        }

                    }

                }

            }
        }
        int from = (page - 1) * size;
        int to = Math.min(page * size, list.size());
        List<BlindBoxShipmentResponse> responses = new ArrayList<>();
        if (from < to) {
            responses = list.subList(from, to);
        }

        PageResponse<BlindBoxShipmentResponse> pageResponse = PageResponse.<BlindBoxShipmentResponse>builder()
                .content(responses)
                .totalElements(list.size())
                .page(page)
                .size(size)
                .totalPages(list.size() / size)
                .last(to == list.size())
                .build();

        return pageResponse;
    }
}
