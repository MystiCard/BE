package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.FeedBackRequest;
import com.example.mysterycard.dto.response.FeedBackResponse;
import com.example.mysterycard.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {UserMapper.class,CardMapper.class,ImageMapper.class})
public interface FeedBackMapper {
    @Mapping(target = "userResponse",source = "buyer")
    @Mapping(target = "cardResponse",source = "orderItem.listSeller.card")
    @Mapping(target = "imageResponses",source = "images")
FeedBackResponse toFeedBackDetailReponse(Feedback feedback);

    Feedback requestToEntity(FeedBackRequest request);


}
