package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.FeedBackRequest;
import com.example.mysterycard.dto.response.FeedBackResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FeedBackService {
  FeedBackResponse createFeedBack(FeedBackRequest feedBackRequest, List<MultipartFile> files);
  Page<FeedBackResponse> getALlFeedBackByUserId(UUID userId, int page, int size);
  boolean checkCanFeedback(UUID orderItemId);
}
