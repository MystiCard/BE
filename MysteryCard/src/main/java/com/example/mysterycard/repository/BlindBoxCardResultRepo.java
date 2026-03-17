package com.example.mysterycard.repository;

import com.example.mysterycard.entity.BlindBox;
import com.example.mysterycard.entity.BlindBoxResult;
import com.example.mysterycard.entity.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BlindBoxCardResultRepo extends JpaRepository<BlindBoxResult, UUID> {
    Page<BlindBoxResult> findByOwner_UserIdAndBlindBox_BlindBoxId(UUID userId, UUID blindBoxId, Pageable pageable);
    @Query("SELECT DISTINCT br.blindBox FROM BlindBoxResult br WHERE br.owner.userId = :userId")
    Page<BlindBox> findDistinctBlindBoxByOwner_UserId(@Param("userId") UUID userId, Pageable pageable);

    Page<BlindBoxResult> findByOwner_UserIdAndStatus(UUID ownerUserId, BlindBoxResult.ResultStatus status, Pageable pageable);

    Page<BlindBoxResult> findByOwner_UserId(UUID ownerUserId, Pageable pageable);

    Page<BlindBoxResult> findByOwner_UserIdAndBlindBox_BlindBoxIdAndStatus(UUID ownerUserId, UUID blindBoxBlindBoxId, BlindBoxResult.ResultStatus status,Pageable pageable);

    List<BlindBoxResult> findByOwner_UserId(UUID ownerUserId);
}
