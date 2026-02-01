package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.Rarity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BlindBoxProbabilitiesResponse {
    private UUID blindBoxId;
    private List<ProbabilityItem> probabilities;

    @Data
    @AllArgsConstructor
    public static class ProbabilityItem {
        private Rarity rarity;
        private double probability; // %
    }
}
