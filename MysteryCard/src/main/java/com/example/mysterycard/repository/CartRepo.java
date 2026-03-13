package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Cart;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CartRepo extends JpaRepository<Cart, UUID> {

    @Query("""
      select  c from Cart c where  c.user = :users and  c.listSeller.status = :status and c.listSeller.quantity > 0


""")
   Page<Cart> findMyCart(Users users, Status status, Pageable pageable );
}
