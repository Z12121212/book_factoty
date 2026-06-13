package com.z.bookcreat.service;

import com.z.bookcreat.dto.NovelOutlineCreateRequest;
import com.z.bookcreat.dto.NovelOutlineUpdateRequest;
import com.z.bookcreat.entity.NovelOutline;

import java.util.List;

public interface NovelOutlineService {

    Long createVersion(NovelOutlineCreateRequest request);

    void update(NovelOutlineUpdateRequest request);

    void confirm(Long id);

    NovelOutline getByIdRequired(Long id);

    NovelOutline getConfirmed(Long novelId, String outlineType);

    List<NovelOutline> listByNovel(Long novelId, String outlineType);
}
