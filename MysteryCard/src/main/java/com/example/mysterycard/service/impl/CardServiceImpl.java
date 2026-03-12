package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.*;
import com.example.mysterycard.dto.response.*;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.Rarity;
import com.example.mysterycard.enums.Status;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.CardMapper;
import com.example.mysterycard.mapper.CardRequiredMapper;
import com.example.mysterycard.mapper.WishListMapper;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.CardService;
import com.example.mysterycard.service.CategoryService;
import com.example.mysterycard.service.NotificationService;
import com.example.mysterycard.service.UserService;
import com.example.mysterycard.specification.CardSpecification;
import com.example.mysterycard.specification.ListSellerSpecification;
import com.example.mysterycard.utils.AIImageUltils;
import com.example.mysterycard.utils.CloudiaryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    @Autowired
    private CardRequiredRepo cardRequiredRepo;
    @Autowired
    private CardRequiredMapper cardRequiredMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private  AIImageUltils aiImageUltils;
  @Autowired
  private CloudiaryUtils cloudiaryUtils;
    @Autowired
    private ImageRepo imageRepo;
    @Autowired
    private ListSellerRepo listSellerRepo;

    @Override
    public CardResponse getCardById(UUID id) {
        Card card = cardRepo.findById(id).orElseThrow(()-> new AppException(ErrorCode.CARD_NOT_FOUND));
        return cardMapper.toResponse(card);
    }

    @Override
    public Page<CardResponse> getAllCards(int page, int size,String search , String sort) {
        Pageable pageable = PageRequest.of(page-1, size);
        Specification<Card> spec = CardSpecification.findByName(search);
            if(sort.equalsIgnoreCase("asc")){
                pageable = PageRequest.of(page-1, size, Sort.by("basePrice").ascending());
            } else {
                pageable = PageRequest.of(page-1, size, Sort.by("basePrice").descending());
            }
        return cardRepo.findAll(spec,pageable).map(cardMapper::toResponse);
    }

    @Override
    public void deleteCardById(UUID id) {
        Card card = cardRepo.findById(id).orElseThrow(()-> new AppException(ErrorCode.CARD_NOT_FOUND));
        cardRepo.delete(card);
    }

    @Override
    public CardResponse createCard(CardRequest request, MultipartFile file)  {
        Category category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if(cardRepo.existsCardByNameAndRarityAndCategory(request.getName(), request.getRarity(), category)){
            throw new AppException(ErrorCode.CARD_DUPLICATE);
        }
        Card card = cardMapper.toCard(request);
            category.addCard(card);
        setMinMaxPrice(card);
        card = cardRepo.save(card);
        if (file != null) {
            String imageUrl = cloudiaryUtils.uploadImage(file);
            Image img = new Image();
            img.setImageUrl(imageUrl);
            img.setCard(card);
           String pGvector = null;
            try {
                pGvector = aiImageUltils.getVector(file.getBytes());
            }catch (Exception e){
                throw new AppException(ErrorCode.IMAGE_CONVERT);
            }
            imageRepo.insertImage(UUID.randomUUID(),card.getCardId(),img.getImageUrl(),pGvector );
        }
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
                        cardRepo.save(card);
                        imageRepo.insertImage(UUID.randomUUID(),card.getCardId(),imgUrl,aiImageUltils.getVectorByURl(imgUrl));
                    }

              //      cardRepo.save(card);
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

    @Override
    public CardRequiredResponse requireNewCard(NewCardRequest request,MultipartFile file) {
        CardRequired cardRequired = new CardRequired();
        cardRequired.setCardName(request.getCardName());
        cardRequired.setRate(request.getRate());
        cardRequired.setBasePrice(request.getBasePrice());
        if(file != null)
        {

            cardRequired.setImageUrl(cloudiaryUtils.uploadImage(file));
        }

        cardRequired.setUsers(userService.getUser());
        Category category = categoryRepo.findByCategoryId(request.getCategoryId());
        if(category==null){
            categoryService.createCate(request.getCategory());
            category = categoryRepo.findByCategoryName(request.getCategory());
        }
        cardRequired.setCategory(category);
        return cardRequiredMapper.toResponse(cardRequiredRepo.save(cardRequired));
    }

    @Override
    @Transactional
    public CardRequiredResponse approveRequest(AddCardRequest request, UUID cardRequestId,MultipartFile file) {
        CardRequired cardRequired = cardRequiredRepo.findByCardRequiredId(cardRequestId)
                .orElseThrow(()-> new AppException(ErrorCode.CARD_REQUIRED_NOT_FOUND));
        if(cardRequired.getStatus()!= CardRequired.RequiredStatus.PENDING){
            throw new AppException(ErrorCode.ALREADY_CREATE);
        }
        cardRequired.setNote(request.getNote());
        cardRequired.setStatus(CardRequired.RequiredStatus.APPROVED);
        cardRequired.setDecidedAt(LocalDateTime.now());
            createCard(CardRequest.builder()
                            .name(cardRequired.getCardName())
                            .rarity(cardRequired.getRate())
                            .basePrice(cardRequired.getBasePrice())
                            .categoryId(cardRequired.getCategory().getCategoryId())
                            .build()
                    ,file);

        notificationService.createNotification("Your card ("+cardRequired.getCardName()+" has been approved ",cardRequired.getUsers(), Notification.NotiType.addCard);
        return cardRequiredMapper.toResponse(cardRequiredRepo.save(cardRequired));
    }

    @Override
    public CardRequiredResponse rejectRequest(AddCardRequest request, UUID cardRequestId) {
        CardRequired cardRequired = cardRequiredRepo.findByCardRequiredId(cardRequestId)
                .orElseThrow(()-> new AppException(ErrorCode.CARD_REQUIRED_NOT_FOUND));
        cardRequired.setNote(request.getNote());
        cardRequired.setStatus(CardRequired.RequiredStatus.REJECTED);
        cardRequired.setDecidedAt(LocalDateTime.now());
        notificationService.createNotification("Your card ("+cardRequired.getCardName()+" has been reject  ",cardRequired.getUsers(), Notification.NotiType.addCard);
        return cardRequiredMapper.toResponse(cardRequiredRepo.save(cardRequired));
    }

    @Override
    public Page<CardRequiredResponse> getAllRequiredByUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Users users = userService.getUser();
        Page<CardRequired> cardRequireds = cardRequiredRepo.findByUsers_userId(users.getUserId(),pageable);
        return cardRequireds.map(cardRequiredMapper::toResponse);
    }

    @Override
    public Page<CardRequiredResponse> gettAllRequireds(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<CardRequired> cardRequireds = cardRequiredRepo.findAll(pageable);
        return cardRequireds.map(cardRequiredMapper::toResponse);
    }

    @Override
    public List<CardResponse> searchByImage(MultipartFile file) {
         Set<Card> cards = new HashSet<>();
        try {
           String pGvector = aiImageUltils.getVector(file.getBytes());
            List<ImageSearchResult> images = imageRepo.searchByImage(pGvector);
            images = images.subList(0,Math.min(images.size(),5));
            images.forEach(image -> {
                cards.add(cardRepo.findByCardId(image.getCardId()));
            });
        }catch (Exception e){
            e.printStackTrace();
            throw new AppException(ErrorCode.IMAGE_CONVERT);
        }
        return cards.stream().map(cardMapper::toResponse).collect(Collectors.toList());
    }
@Transactional
    @Override
    public PageResponse<CardSellResponse> getALlCarSelling(int page, int size, CardSellRequest request) {
       page = page -1;

        Sort sort1 = Sort.by(Sort.Direction.ASC, "price");
        if(request.getSort() != null && request.getSort().equalsIgnoreCase("desc")){
            sort1 = Sort.by(Sort.Direction.DESC, "price");
        }
        Specification<ListSeller> specification = Specification.allOf(

                ListSellerSpecification.findByStatus(Status.AVAILABLE),
                ListSellerSpecification.findByQuanity(),
                ListSellerSpecification.findByCardName(request.getKeyword()),
                ListSellerSpecification.findByCardPrice(request.getMin(),request.getMax()),
                ListSellerSpecification.findByRarityCard(request.getRarity())

        );
        List<ListSeller> listSellers = listSellerRepo.findAll(specification,sort1);
        log.info("LIst seller {}",listSellers.size());
       listSellers.forEach((l)->  log.info("listSellers {}",l.getListSellerId()));
        Set<CardSellResponse> list = new HashSet<>();
        if(listSellers.size()>0){
            listSellers.forEach(ls->{
                CardSellResponse cardSellResponse = CardSellResponse.builder()
                        .cardResponse(cardMapper.toResponse(ls.getCard()))
                        .numberOfCard(listSellerRepo.totalNumberCard(ls.getCard().getCardId(),Status.AVAILABLE))
                        .numberOfSeller(listSellerRepo.countByStatusAndCard(Status.AVAILABLE,ls.getCard()))
                        .build();
                list.add(cardSellResponse);
            });
        }
     int to = Math.min(list.size(), (page+1)*size);
        List<CardSellResponse> responses = new ArrayList<>();
        if(list.size()> 0){
            responses = list.stream().toList().subList(page*size,to);
        }


        return PageResponse.<CardSellResponse>builder()
                .content(responses)
                .totalElements(list.size())
                .page(page)
                .size(size)
                .totalPages(list.size()/size)
                .last(to == list.size())
                .build();
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
    public Page<WishListResponse> getUserWishList(int page , int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("expectPrice").descending());
        Page<WishList> wishLists = wishListRepo.findByUser_UserId(userService.getMyInfor().getUserId(), pageable);
        return wishLists.map(wishListMapper::toResponse);
    }
}
