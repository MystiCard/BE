package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.ReturnRequestdto;
import com.example.mysterycard.dto.response.ReturnResponse;
import com.example.mysterycard.entity.ReturnRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {OrderItemMapper.class,ImageMapper.class})
public interface ReturnRequestMapper {
    @Mapping(source = "orderItemList", target = "orderItems")
    @Mapping(target = "listImages",source = "images")
    // người mua
    @Mapping(source = "buyer.userId", target = "buyerId")
    @Mapping(source = "buyer.name", target = "buyerName")
    // người bán: lấy từ orderItem đầu tiên trong danh sách bằng biểu thức Java
    @Mapping(target = "sellerId",
             expression = "java(returnRequest.getOrderItemList() != null && !returnRequest.getOrderItemList().isEmpty() ? " +
                     "returnRequest.getOrderItemList().get(0).getListSeller().getSeller().getUserId() : null)")
    @Mapping(target = "sellerName",
             expression = "java(returnRequest.getOrderItemList() != null && !returnRequest.getOrderItemList().isEmpty() ? " +
                     "returnRequest.getOrderItemList().get(0).getListSeller().getSeller().getName() : null)")
    ReturnResponse enityToReturnResponse(ReturnRequest returnRequest);
    ReturnRequest requestToEntity(ReturnRequestdto request);
}
