package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.RateConfigRequest;
import com.example.mysterycard.dto.response.RateConfigResponse;
import com.example.mysterycard.entity.RateConfig;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.RateConfigMapper;
import com.example.mysterycard.repository.RateConfigRepo;
import com.example.mysterycard.service.RateConfigService;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RateConfigServiceImpl implements RateConfigService {
    @Autowired
    private RateConfigRepo rateConfigRepo;
    @Autowired
    private RateConfigMapper rateConfigMapper;
    @Override
    public RateConfigResponse createRateConfig(RateConfigRequest request) {
        if(rateConfigRepo.existsByCardRarity(request.getCardRarity())){
            throw new AppException(ErrorCode.RATE_CONFIG_ALREADY_EXISTS);
        }
        RateConfig rateConfig = rateConfigMapper.toEntity(request);
        rateConfig = rateConfigRepo.save(rateConfig);
        return rateConfigMapper.toResponse(rateConfig);
    }

    @Override
    public List<RateConfigResponse> getAllRateConfigs() {
       List<RateConfig> rateConfigs = rateConfigRepo.findAll();
         return rateConfigs.stream()
                .map(rateConfigMapper::toResponse)
                .toList();
    }

    @Override
    public RateConfigResponse updateRateConfig(UUID id, RateConfigRequest request) {
        RateConfig rateConfig = rateConfigRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RATE_CONFIG_NOT_FOUND));
        rateConfig.setDropRate(request.getDropRate());
        rateConfig = rateConfigRepo.save(rateConfig);
        return rateConfigMapper.toResponse(rateConfig);
    }

    @Override
    public RateConfigResponse getRateConfigById(UUID id) {
        RateConfig rateConfig = rateConfigRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RATE_CONFIG_NOT_FOUND));
        return rateConfigMapper.toResponse(rateConfig);
    }

    @Override
    public void deleteRateConfig(UUID id) {
        RateConfig rateConfig = rateConfigRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RATE_CONFIG_NOT_FOUND));
        rateConfigRepo.delete(rateConfig);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Integer> importRateConfigs(MultipartFile file) {
        int addCount = 0;
        int skipCount = 0;
        DataFormatter dataFormatter = new DataFormatter();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua tiêu đề

                // Đọc dữ liệu từ các cột
                String rarityStr = dataFormatter.formatCellValue(row.getCell(0)).trim().toUpperCase();
                String dropRateStr = dataFormatter.formatCellValue(row.getCell(1)).trim();
                String varianceStr = dataFormatter.formatCellValue(row.getCell(2)).trim();

                if (rarityStr.isEmpty() || dropRateStr.isEmpty()) continue;

                // Kiểm tra nếu Rarity này đã tồn tại trong hệ thống thì bỏ qua hoặc cập nhật
                if (rateConfigRepo.existsByCardRarity(com.example.mysterycard.enums.Rarity.valueOf(rarityStr))) {
                    skipCount++;
                    continue;
                }

                try {
                    RateConfig rateConfig = RateConfig.builder()
                            .cardRarity(com.example.mysterycard.enums.Rarity.valueOf(rarityStr))
                            .dropRate(Double.parseDouble(dropRateStr))
                            .variancePercent(varianceStr.isEmpty() ? 0 : Double.parseDouble(varianceStr))
                            .build();

                    rateConfigRepo.save(rateConfig);
                    addCount++;
                } catch (Exception e) {
                    skipCount++; // Lỗi parse dữ liệu dòng này
                }
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_IMPORT_ERROR);
        }

        return Map.of("added", addCount, "skipped", skipCount);
    }
}
