package com.example.mysterycard.dto.response;

import com.example.mysterycard.entity.Users;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Builder
@Data
public class FeedBackResponse {
        private UUID feedBackId;
        private String comment;
        private int rating;
        private LocalDateTime createdAt ;
        private UserResponse userResponse;
        private CardResponse cardResponse;
        private List<ImageResponse> imageResponses;


}
