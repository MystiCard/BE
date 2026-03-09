package com.example.mysterycard.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequest {
    private String categoryName;
}
