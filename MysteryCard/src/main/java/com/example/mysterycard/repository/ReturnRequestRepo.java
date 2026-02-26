package com.example.mysterycard.repository;

import com.example.mysterycard.dto.request.ReturnRequestdto;
import com.example.mysterycard.entity.ReturnRequest;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.ReturnRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface ReturnRequestRepo extends JpaRepository<ReturnRequest, UUID> {

    Page<ReturnRequest> findByBuyerAndStatus(Users buyer, ReturnRequestStatus status, Pageable pageable);
    Page<ReturnRequest> findByBuyer(Users buyer, Pageable pageable);
}
