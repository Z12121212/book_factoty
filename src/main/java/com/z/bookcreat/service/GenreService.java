package com.z.bookcreat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.z.bookcreat.dto.GenreCreateRequest;
import com.z.bookcreat.dto.GenreUpdateRequest;
import com.z.bookcreat.entity.Genre;

import java.util.List;

public interface GenreService {

    Long create(GenreCreateRequest request);

    void update(GenreUpdateRequest request);

    void setEnabled(Long id, boolean enabled);

    Genre getByIdRequired(Long id);

    List<Genre> listEnabled();

    Page<Genre> page(long current, long size, String keyword);
}
