package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.CardRequest;
import com.example.mysterycard.dto.request.WishListRequest;
import com.example.mysterycard.dto.response.CardResponse;
import com.example.mysterycard.dto.response.WishListResponse;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.Rarity;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.CardMapper;
import com.example.mysterycard.mapper.WishListMapper;
import com.example.mysterycard.repository.CardRepo;
import com.example.mysterycard.repository.CategoryRepo;
import com.example.mysterycard.repository.RateConfigRepo;
import com.example.mysterycard.repository.WishListRepo;
import com.example.mysterycard.service.CardService;
import com.example.mysterycard.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class CardServiceImpl implements CardService {
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private RateConfigRepo rateConfigRepo;
    @Autowired
    private WishListMapper wishListMapper;
    @Autowired
    private WishListRepo wishListRepo;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private UserService userService;
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
        Category category = categoryRepo.findById(UUID.fromString(request.getCategoryId()))
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if(cardRepo.existsCardByNameAndRarityAndCategory(request.getName(), request.getRarity(), category)){
            throw new AppException(ErrorCode.CARD_DUPLICATE);
        }
        Card card = cardMapper.toCard(request);
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


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Integer> importCards(MultipartFile file) {
        int addCount = 0;
        int skipCount = 0;

        Map<String, Category> categoryCache = new HashMap<>();
        DataFormatter dataFormatter = new DataFormatter();

        // Tải trước RateConfig vào memory để tránh query DB hàng nghìn lần trong hàm setMinMaxPrice
        List<RateConfig> allConfigs = rateConfigRepo.findAll();

        Set<String> existingCardsSet = new HashSet<>(cardRepo.findAll().stream()
                .map(c -> (c.getName() + "|" + c.getRarity() + "|" + c.getCategory().getCategoryName()).toLowerCase())
                .toList());

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua Header

                try {
                    // 1. Đọc dữ liệu thô
                    String name = dataFormatter.formatCellValue(row.getCell(0)).trim();
                    String rarityStr = dataFormatter.formatCellValue(row.getCell(1)).trim();
                    String imgUrl = dataFormatter.formatCellValue(row.getCell(2)).trim();
                    String priceStr = dataFormatter.formatCellValue(row.getCell(4)).trim().replace(",", "");
                    String categoryName = dataFormatter.formatCellValue(row.getCell(5)).trim();

                    // 2. Validate nhanh
                    if (name.isEmpty() || rarityStr.isEmpty() || priceStr.isEmpty()) continue;

                    Category category = categoryCache.computeIfAbsent(categoryName.toLowerCase(), k -> {
                        Category existing = categoryRepo.findByCategoryName(categoryName);
                        return (existing != null) ? existing : categoryRepo.save(Category.builder().categoryName(categoryName).build());
                    });
                    // Kiểm tra trùng lặp trong DB
                    String currentKey = (name + "|" + rarityStr + "|" + category.getCategoryName()).toLowerCase();
                    if (existingCardsSet.contains(currentKey)) {
                        skipCount++;
                        continue;
                    }

                    // 3. Xử lý Rarity & Price (Parse an toàn)
                    Rarity rarity = Rarity.valueOf(rarityStr.toUpperCase());
                    double basePrice = Double.parseDouble(priceStr);

                    // 4. Xử lý Category (Dùng Cache)


                    // 5. Khởi tạo Card
                    Card card = new Card();
                    card.setName(name);
                    card.setRarity(rarity);
                    card.setBasePrice(basePrice);
                    card.setCategory(category); // Đảm bảo set category ngay

                    // 6. Tính Min/Max Price ngay tại chỗ (Không gọi hàm cũ để tránh n+1 query)
                    RateConfig config = allConfigs.stream()
                            .filter(rc -> rc.getCardRarity() == rarity)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Rate config missing for: " + rarity));

                    card.setMinPrice(basePrice * (1 - config.getVariancePercent()));
                    card.setMaxPrice(basePrice * (1 + config.getVariancePercent()));

                    // 7. Xử lý Image
                    if (!imgUrl.isEmpty()) {
                        Image img = new Image();
                        img.setImageUrl(imgUrl);
                        img.setCard(card);
                        card.setImages(new ArrayList<>(List.of(img)));
                    }

                    cardRepo.save(card);
                    addCount++;

                } catch (Exception e) {
                    // Log lỗi cho dòng cụ thể nhưng không làm sập cả quá trình import
                    System.err.println("Lỗi tại dòng " + row.getRowNum() + ": " + e.getMessage());
                    skipCount++;
                }
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_IMPORT_ERROR);
        }

        return Map.of("added", addCount, "skipped", skipCount);
    }

    public WishListResponse addToWishList(UUID cardId, WishListRequest request) {
        Card card = cardRepo.findById(cardId).orElseThrow(() -> new AppException(ErrorCode.CARD_NOT_FOUND));
        WishList wishList = wishListMapper.toEntity(request);
        Users user = userService.getUser();
        wishList.setUser(user);
        user.getWishLists().add(wishList);
        wishList.setCard(card);
        WishList savedWishList = wishListRepo.save(wishList);
        return wishListMapper.toResponse(savedWishList);
    }

    public void removeFromWishList(UUID wishListId) {
        WishList wishList = wishListRepo.findById(wishListId).orElseThrow(() -> new AppException(ErrorCode.WISHLIST_NOT_FOUND));
        wishListRepo.delete(wishList);
    }

    public WishListResponse changeExpectPrice(UUID wishListId, Long newExpectPrice) {
        WishList wishList = wishListRepo.findById(wishListId).orElseThrow(() -> new AppException(ErrorCode.WISHLIST_NOT_FOUND));
        wishList.setExpectPrice(newExpectPrice);
        WishList updatedWishList = wishListRepo.save(wishList);
        return wishListMapper.toResponse(updatedWishList);
    }
    public List<WishListResponse> getUserWishList() {
        Users user = userService.getUser();
        List<WishList> wishLists = user.getWishLists();
        return wishLists.stream().map(wishListMapper::toResponse).toList();
    }
}
