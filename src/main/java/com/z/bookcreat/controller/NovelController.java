package com.z.bookcreat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.z.bookcreat.common.ApiResponse;
import com.z.bookcreat.dto.NovelCreateRequest;
import com.z.bookcreat.dto.NovelStatusRequest;
import com.z.bookcreat.dto.NovelUpdateRequest;
import com.z.bookcreat.entity.Novel;
import com.z.bookcreat.service.NovelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/novels")
public class NovelController {

    private final NovelService novelService;

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody NovelCreateRequest request) {
        return ApiResponse.ok(novelService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody NovelUpdateRequest request) {
        request.setId(id);
        novelService.update(request);
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody NovelStatusRequest request) {
        request.setId(id);
        novelService.updateStatus(request);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}")
    public ApiResponse<Novel> detail(@PathVariable Long id) {
        return ApiResponse.ok(novelService.getByIdRequired(id));
    }

    @GetMapping
    public ApiResponse<Page<Novel>> page(@RequestParam(defaultValue = "1") long current,
                                         @RequestParam(defaultValue = "20") long size,
                                         @RequestParam(required = false) Long userId,
                                         @RequestParam(required = false) String status) {
        return ApiResponse.ok(novelService.page(current, size, userId, status));
    }
}
