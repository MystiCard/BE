package com.example.mysterycard.schedule;

import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.Summaries;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.repository.SumariesRepository;
import com.example.mysterycard.repository.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SumariesSchedule {
    private final SumariesRepository sumariesRepository;
    private final TransactionRepo transactionRepo;
    @Scheduled(cron = "0 1 0 * * ?")
    @Transactional
    public  void generateReport()
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        Summaries summaries = Summaries.builder()
                .localDate(LocalDate.from(localDateTime))
                .totalPayment(transactionRepo.countByCreateAtBetween(localDateTime.minusDays(1),localDateTime))
                .error(transactionRepo.countByCreateAtBetweenAndStatusTransaction(localDateTime.minusDays(1),localDateTime, StatusPayment.FAILED))
                .success(transactionRepo.countByCreateAtBetweenAndStatusTransaction(localDateTime.minusDays(1),localDateTime, StatusPayment.SUCCESS))
                .totalAmount(transactionRepo.sumAmount(localDateTime.minusDays(1),localDateTime))
            .build();
        sumariesRepository.save(summaries);
    }

}
