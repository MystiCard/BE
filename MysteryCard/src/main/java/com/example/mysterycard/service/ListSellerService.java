package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.SellRequest;
import com.example.mysterycard.dto.response.ListSellerResponse;
import com.example.mysterycard.dto.response.SellResponse;
import com.example.mysterycard.enums.Status;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ListSellerService {
    SellResponse createListSeller(SellRequest request, UUID cardId);
    Page<SellResponse> getListSellersByCardId(UUID cardId, int page, int size);
    Page<ListSellerResponse> getListingByUserId(UUID userId, int page, int size);
    SellResponse updateListseller(UUID id , SellRequest request);
    void updateStatus(UUID id, Status status);
}
