package com.z.bookcreat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.z.bookcreat.dto.NovelCreateRequest;
import com.z.bookcreat.dto.NovelStatusRequest;
import com.z.bookcreat.dto.NovelUpdateRequest;
import com.z.bookcreat.entity.Novel;

public interface NovelService {

    Long create(NovelCreateRequest request);

    void update(NovelUpdateRequest request);

    void updateStatus(NovelStatusRequest request);

    Novel getByIdRequired(Long id);

    Page<Novel> page(long current, long size, Long userId, String status);
}
