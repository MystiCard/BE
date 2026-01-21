package com.example.mysterycard.utils;

import com.cloudinary.utils.ObjectUtils;
import com.example.mysterycard.configuration.CloudiaryConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CloudiaryUtils {
    private final CloudiaryConfig cloudiaryConfig;
        public String uploadImage(MultipartFile file) {
            try {
                String name = UUID.randomUUID().toString()+file.getOriginalFilename();
                Map uploadResult = cloudiaryConfig.cloudinary().uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder", "mystiCard/images",
                                "resource_type", "image",
                                "public_id", name
                        )
                );
                return uploadResult.get("secure_url").toString();
            } catch (Exception e) {
                throw new RuntimeException("Upload failed", e);
            }
        }
    }



