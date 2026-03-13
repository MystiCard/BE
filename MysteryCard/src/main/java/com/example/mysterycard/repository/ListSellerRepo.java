package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Card;
import com.example.mysterycard.entity.ListSeller;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ListSellerRepo extends JpaRepository<ListSeller, UUID>, JpaSpecificationExecutor<ListSeller> {
    Page<ListSeller> findByCard_CardId(UUID cardId, Pageable pageable);

    Page<ListSeller> findAllBySeller(Users seller, Pageable pageable);

    @Query(value = """
      select  sum(quantity) from ListSeller where  card.cardId = :cardId and status = :status
""")
    int totalNumberCard(UUID cardId, Status status);

    int countByStatusAndCard(Status status, Card card);

    Page<ListSeller> findByCard_CardIdAndStatusAndQuantityGreaterThan(UUID cardCardId, Status status, int quantityIsGreaterThan,Pageable pageable);

    Page<ListSeller> findByCard_CardIdAndStatusAndQuantityGreaterThanEqual(UUID cardCardId, Status status, int quantityIsGreaterThan,Pageable pageable);

    ListSeller findByListSellerId(UUID listSellerId);

    Page<ListSeller> findByCard_CardIdAndStatusAndQuantityGreaterThanEqualAndSellerIsNot(UUID cardCardId, Status status, int quantityIsGreaterThan, Users seller,Pageable pageable);
}
