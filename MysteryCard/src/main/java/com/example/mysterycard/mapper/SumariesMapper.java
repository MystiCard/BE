package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.TransactionReportResponse;
import com.example.mysterycard.entity.Summaries;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SumariesMapper {
    TransactionReportResponse.SummariesResponse entityToResponse(Summaries summary);
}
