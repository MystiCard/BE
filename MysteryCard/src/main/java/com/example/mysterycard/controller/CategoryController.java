package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.CategoryRequest;
import com.example.mysterycard.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cate")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    @GetMapping("{id}")
    public ApiResponse getCategoryById(@PathVariable UUID id) {
        return ApiResponse.success(categoryService.getCateById(id));
    }

    @GetMapping
    public ApiResponse getCategories() {
        return ApiResponse.success(categoryService.getCategories());
    }

    @DeleteMapping("{id}")
    public ApiResponse deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCate(id);
        return ApiResponse.success();
    }

    @PostMapping
    public ApiResponse createCategory(@RequestBody CategoryRequest request) {
        return ApiResponse.success(categoryService.createCate(request.getCategoryName()));
    }

    @PutMapping("{id}")
    public ApiResponse updateCategory(@PathVariable UUID id, @RequestBody CategoryRequest request) {
        return ApiResponse.success(categoryService.updateCate(id, request.getCategoryName()));
    }

}
