package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.FeedBackRequest;
import com.example.mysterycard.dto.response.FeedBackResponse;
import com.example.mysterycard.entity.Feedback;
import com.example.mysterycard.entity.Image;
import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.OrderItemStatus;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.FeedBackMapper;
import com.example.mysterycard.repository.FeedBackRepo;
import com.example.mysterycard.repository.ImageRepo;
import com.example.mysterycard.repository.OrderItemsRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.FeedBackService;
import com.example.mysterycard.service.UserService;
import com.example.mysterycard.utils.CloudiaryUtils;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class FeedBackServiceImpl implements FeedBackService {
 private final FeedBackMapper feedBackMapper;
 private final FeedBackRepo feedBackRepo;
 private final OrderItemsRepo orderItemsRepo;
 private final UsersRepo usersRepo;
 private final CloudiaryUtils cloudiaryUtils;
 private final ImageRepo imageRepo;
    @Override
    public FeedBackResponse createFeedBack(FeedBackRequest feedBackRequest, List<MultipartFile> files) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = null;
        if(email == null)
        {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        users = usersRepo.findByEmail(email);
        if (users == null)
        {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        OrderItem orderItem = orderItemsRepo.findById(feedBackRequest.getOrderItemId()).orElseThrow(
                ()-> new AppException(ErrorCode.ORDER_ITEMS_NOT_FOUND)
        );
        Feedback feedback = feedBackMapper.requestToEntity(feedBackRequest);
        feedback.setOrderItem(orderItem);
        feedback.setBuyer(users);
        feedback.setSeller(orderItem.getListSeller().getSeller());

        if(files != null && files.size() > 0)
        {
            for(MultipartFile file : files)
            {
                Image image = Image.builder()
                        .imageUrl(cloudiaryUtils.uploadImage(file))
                        .feedback(feedback)
                        .build();
                feedback.getImages().add(image);
            }
        }
        return feedBackMapper.toFeedBackDetailReponse( feedBackRepo.save(feedback)) ;
    }

    @Override
    public Page<FeedBackResponse> getALlFeedBackByUserId(UUID userId, int page, int size) {
       Users users = null;
        if(userId != null)
        {
             users = usersRepo.findByUserId(userId);
        }
        if(userId == null)
        {
            throw  new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());

        Page<Feedback> feedbacks = feedBackRepo.findBySeller(users,pageable);
        return feedbacks.map(feedBackMapper::toFeedBackDetailReponse);
    }

    @Override
    public boolean checkCanFeedback(UUID orderItemId) {
        OrderItem orderItem = orderItemsRepo.findById(orderItemId).orElseThrow(
                ()->  new AppException(ErrorCode.ORDER_ITEMS_NOT_FOUND)
        );
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = null;
        if(email == null)
        {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        users = usersRepo.findByEmail(email);
        if (users == null)
        {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        Users buyer = orderItem.getOrder().getBuyer();

        return orderItem.getOrderItemStatus().equals(OrderItemStatus.RECIEVED)
                && buyer.equals(users)
                && !feedBackRepo.existsByOrderItemAndBuyer(orderItem,buyer);
    }
}
