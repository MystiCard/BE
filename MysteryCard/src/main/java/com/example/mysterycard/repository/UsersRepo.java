package com.example.mysterycard.repository;

import com.example.mysterycard.dto.response.UserResponse;
import com.example.mysterycard.entity.Shipment;
import com.example.mysterycard.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface UsersRepo extends JpaRepository<Users, String> {
    Users findByEmail(String email);

    boolean existsByEmail(String email);

    Users findByUserId(UUID userId);

    boolean existsByPhone(String phone);

    Page<Users> getALlByActive(boolean active, Pageable pageable);
@Query("""
select u from Users u where (u.userId not in :uuidList or u.shipmentList is empty or :uuidList is null) and u.userId in :userIds

  

""")
    Page<Users> findAllByUserIdIsNotInAndShipmentListNull(List<UUID> uuidList,Pageable pageable, List<UUID> userIds);
}
