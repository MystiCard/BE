package com.example.mysterycard.repository;

import com.example.mysterycard.dto.response.ImageSearchResult;
import com.example.mysterycard.entity.Image;
import com.pgvector.PGvector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ImageRepo extends JpaRepository<Image, UUID> {

    @Query(value = """
SELECT image_id, card_id, image_url, return_request_id, tracking_id
FROM image
WHERE card_id IS NOT NULL
ORDER BY embedding <=> CAST(:pgVector AS vector)
LIMIT 5
""", nativeQuery = true)
    List<ImageSearchResult> searchByImage(@Param("pgVector") String pgVector);
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO image(image_id, card_id, image_url, embedding)
        VALUES (:id, :cardId, :url, CAST(:embedding AS vector))
    """, nativeQuery = true)
    void insertImage(
            UUID id,
            UUID cardId,
            String url,
            String embedding
    );
}
