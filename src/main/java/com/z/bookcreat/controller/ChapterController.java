package com.z.bookcreat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.z.bookcreat.common.ApiResponse;
import com.z.bookcreat.dto.ChapterCreateRequest;
import com.z.bookcreat.dto.ChapterUpdateRequest;
import com.z.bookcreat.entity.Chapter;
import com.z.bookcreat.service.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody ChapterCreateRequest request) {
        return ApiResponse.ok(chapterService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ChapterUpdateRequest request) {
        request.setId(id);
        chapterService.update(request);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}")
    public ApiResponse<Chapter> detail(@PathVariable Long id) {
        return ApiResponse.ok(chapterService.getByIdRequired(id));
    }

    @GetMapping
    public ApiResponse<Page<Chapter>> page(@RequestParam Long novelId,
                                           @RequestParam(defaultValue = "1") long current,
                                           @RequestParam(defaultValue = "50") long size) {
        return ApiResponse.ok(chapterService.page(novelId, current, size));
    }
}
