package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.CartRequest;
import com.example.mysterycard.dto.response.CartResponse;
import com.example.mysterycard.service.CartService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;


    @PostMapping
    public ApiResponse<CartResponse> create (@RequestBody CartRequest request)
    {
        return ApiResponse.success(cartService.addToCard(request));
    }
    @GetMapping()
    public ApiResponse<Page<CartResponse>> getALl(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(cartService.getALl(page, size));
    }
    @PutMapping("/{id}")
    public ApiResponse<CartResponse> udpate(
            @PathVariable UUID id,
            @RequestBody CartRequest request
            ) {
        return ApiResponse.success(cartService.update(request, id));
    }
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(
            @PathVariable UUID id
    ) {
        cartService.remove( id);
        return ApiResponse.success("Delete Cart Sucessfully");
    }


}
