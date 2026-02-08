package com.example.mysterycard.controller;

import com.example.mysterycard.utils.CloudiaryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final CloudiaryUtils cloudiaryUtils;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(cloudiaryUtils.uploadImage(file));

    }
}
