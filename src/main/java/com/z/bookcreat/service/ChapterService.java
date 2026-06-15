package com.z.bookcreat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.z.bookcreat.dto.ChapterCreateRequest;
import com.z.bookcreat.dto.ChapterUpdateRequest;
import com.z.bookcreat.entity.Chapter;

public interface ChapterService {

    Long create(ChapterCreateRequest request);

    void update(ChapterUpdateRequest request);

    Chapter getByIdRequired(Long id);

    Page<Chapter> page(Long novelId, long current, long size);

    void saveGeneratedContent(Long chapterId, String title, String content);
}
