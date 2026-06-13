package com.z.bookcreat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.z.bookcreat.common.ApiResponse;
import com.z.bookcreat.dto.GenreCreateRequest;
import com.z.bookcreat.dto.GenreUpdateRequest;
import com.z.bookcreat.entity.Genre;
import com.z.bookcreat.service.GenreService;
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
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody GenreCreateRequest request) {
        return ApiResponse.ok(genreService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody GenreUpdateRequest request) {
        request.setId(id);
        genreService.update(request);
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/enabled")
    public ApiResponse<Void> setEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        genreService.setEnabled(id, enabled);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}")
    public ApiResponse<Genre> detail(@PathVariable Long id) {
        return ApiResponse.ok(genreService.getByIdRequired(id));
    }

    @GetMapping("/enabled")
    public ApiResponse<List<Genre>> listEnabled() {
        return ApiResponse.ok(genreService.listEnabled());
    }

    @GetMapping
    public ApiResponse<Page<Genre>> page(@RequestParam(defaultValue = "1") long current,
                                         @RequestParam(defaultValue = "20") long size,
                                         @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(genreService.page(current, size, keyword));
    }
}
