package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.FeedBackRequest;
import com.example.mysterycard.dto.response.FeedBackResponse;
import com.example.mysterycard.entity.Feedback;
import com.example.mysterycard.service.FeedBackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedBackController {
    private final FeedBackService feedBackService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FeedBackResponse>> createFeedback(@RequestPart FeedBackRequest feedback,
    @RequestPart(required = false) List<MultipartFile> file
    ) {
        return ResponseEntity.ok(ApiResponse.success(feedBackService.createFeedBack(feedback,file)));
    }
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<Page<FeedBackResponse>>> findById(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1") int size) {
        return ResponseEntity.ok(ApiResponse.success(feedBackService.getALlFeedBackByUserId(userId,page,size)));
    }
    @GetMapping("/check-can-feedback/{orderItemsId}")
    public ResponseEntity<ApiResponse<Boolean>> findByOrderId(@PathVariable UUID orderItemsId) {
        return ResponseEntity.ok(ApiResponse.success(feedBackService.checkCanFeedback(orderItemsId)));

    }
}
