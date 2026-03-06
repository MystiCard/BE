package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.NotificationResponse;
import com.example.mysterycard.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "card.cardId", target = "cardId")
    @Mapping(source = "users.userId", target = "userId")
    NotificationResponse toResponse(Notification notification);
}
