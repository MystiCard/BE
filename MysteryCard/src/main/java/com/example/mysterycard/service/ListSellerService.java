package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.SellRequest;
import com.example.mysterycard.dto.response.SellResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ListSellerService {
    SellResponse createListSeller(SellRequest request, UUID cardId);
    Page<SellResponse> getListSellersByCardId(UUID cardId, int page, int size);
}
