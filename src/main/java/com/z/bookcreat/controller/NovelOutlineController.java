package com.z.bookcreat.controller;

import com.z.bookcreat.common.ApiResponse;
import com.z.bookcreat.dto.NovelOutlineCreateRequest;
import com.z.bookcreat.dto.NovelOutlineUpdateRequest;
import com.z.bookcreat.entity.NovelOutline;
import com.z.bookcreat.service.NovelOutlineService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/outlines")
public class NovelOutlineController {

    private final NovelOutlineService novelOutlineService;

    @PostMapping
    public ApiResponse<Long> createVersion(@Valid @RequestBody NovelOutlineCreateRequest request) {
        return ApiResponse.ok(novelOutlineService.createVersion(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody NovelOutlineUpdateRequest request) {
        request.setId(id);
        novelOutlineService.update(request);
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/confirm")
    public ApiResponse<Void> confirm(@PathVariable Long id) {
        novelOutlineService.confirm(id);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}")
    public ApiResponse<NovelOutline> detail(@PathVariable Long id) {
        return ApiResponse.ok(novelOutlineService.getByIdRequired(id));
    }

    @GetMapping("/confirmed")
    public ApiResponse<NovelOutline> getConfirmed(@RequestParam Long novelId, @RequestParam String outlineType) {
        return ApiResponse.ok(novelOutlineService.getConfirmed(novelId, outlineType));
    }

    @GetMapping
    public ApiResponse<List<NovelOutline>> list(@RequestParam Long novelId,
                                                @RequestParam(required = false) String outlineType) {
        return ApiResponse.ok(novelOutlineService.listByNovel(novelId, outlineType));
    }
}
