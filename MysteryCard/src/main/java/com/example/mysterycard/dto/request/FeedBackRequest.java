package com.example.mysterycard.dto.request;

import com.example.mysterycard.entity.Users;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FeedBackRequest {
    private String comment;
    private int rating;
    private UUID orderItemId;
}
