package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.ReturnRequestdto;
import com.example.mysterycard.dto.response.PageResponse;
import com.example.mysterycard.dto.response.RetrunRequestCanDoResponse;
import com.example.mysterycard.dto.response.ReturnResponse;
import com.example.mysterycard.enums.ReturnRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ReturnRequestService {
     ReturnResponse cancleReturnRequest(UUID returnRequestId);
     ReturnResponse sendReturnRequest(ReturnRequestdto request, List<MultipartFile> fileList);
     Page<ReturnResponse> getMySendReturnRequest(ReturnRequestStatus status, int page, int size);
     PageResponse<ReturnResponse> receiveReturnRequest(ReturnRequestStatus status, int page, int size);
     ReturnResponse approveReturnRequest(UUID returnRequestId);
     boolean canSendReturn(UUID orderItemsId);
     ReturnResponse rejectReturnReqeust(UUID retrurnRequestId);
     RetrunRequestCanDoResponse canDo(UUID returnRequestId);
     ReturnResponse confirmRecieved(UUID returnId);
}
