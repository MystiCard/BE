package com.example.mysterycard.repository;

import com.example.mysterycard.dto.request.ReturnRequestdto;
import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.entity.ReturnRequest;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.ReturnRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


public interface ReturnRequestRepo extends JpaRepository<ReturnRequest, UUID> {

    Page<ReturnRequest> findByBuyerAndStatus(Users buyer, ReturnRequestStatus status, Pageable pageable);
    Page<ReturnRequest> findByBuyer(Users buyer, Pageable pageable);

    boolean existsByBuyerAndOrderItemList(Users buyer, List<OrderItem> orderItemList);

    boolean existsByBuyerAndOrderItemListAndStatusIsNot(Users buyer, List<OrderItem> orderItemList, ReturnRequestStatus status);

    boolean existsByBuyerAndOrderItemListAndStatusIsNotIn(Users buyer, List<OrderItem> orderItemList, Collection<ReturnRequestStatus> statuses);
}
