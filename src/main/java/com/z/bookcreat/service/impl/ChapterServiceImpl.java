package com.z.bookcreat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.z.bookcreat.dto.ChapterCreateRequest;
import com.z.bookcreat.dto.ChapterUpdateRequest;
import com.z.bookcreat.entity.Chapter;
import com.z.bookcreat.mapper.ChapterMapper;
import com.z.bookcreat.service.ChapterService;
import com.z.bookcreat.service.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterMapper chapterMapper;
    private final NovelService novelService;

    @Override
    @Transactional
    public Long create(ChapterCreateRequest request) {
        novelService.getByIdRequired(request.getNovelId());
        ensureUniqueChapterNo(request.getNovelId(), request.getChapterNo(), null);

        LocalDateTime now = LocalDateTime.now();
        Chapter chapter = new Chapter();
        chapter.setNovelId(request.getNovelId());
        chapter.setVolumeId(request.getVolumeId());
        chapter.setChapterNo(request.getChapterNo());
        chapter.setTitle(request.getTitle());
        chapter.setOutline(request.getOutline());
        chapter.setWordCount(0);
        chapter.setStatus("pending");
        chapter.setCreatedAt(now);
        chapter.setUpdatedAt(now);
        chapterMapper.insert(chapter);
        return chapter.getId();
    }

    @Override
    @Transactional
    public void update(ChapterUpdateRequest request) {
        getByIdRequired(request.getId());
        Chapter chapter = new Chapter();
        chapter.setId(request.getId());
        chapter.setTitle(request.getTitle());
        chapter.setOutline(request.getOutline());
        chapter.setUpdatedAt(LocalDateTime.now());
        chapterMapper.updateById(chapter);
    }

    @Override
    public Chapter getByIdRequired(Long id) {
        Chapter chapter = chapterMapper.selectById(id);
        if (chapter == null) {
            throw new IllegalArgumentException("章节不存在");
        }
        return chapter;
    }

    @Override
    public Page<Chapter> page(Long novelId, long current, long size) {
        if (novelId != null) {
            novelService.getByIdRequired(novelId);
        }
        return chapterMapper.selectPage(new Page<>(current, size), new LambdaQueryWrapper<Chapter>()
                .eq(novelId != null, Chapter::getNovelId, novelId)
                .orderByAsc(Chapter::getChapterNo));
    }

    @Override
    @Transactional
    public void saveGeneratedContent(Long chapterId, String title, String content) {
        Chapter existed = getByIdRequired(chapterId);
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setTitle(title != null && !title.isBlank() ? title : existed.getTitle());
        chapter.setContent(content);
        chapter.setWordCount(content == null ? 0 : content.length());
        chapter.setStatus("completed");
        chapter.setUpdatedAt(LocalDateTime.now());
        chapterMapper.updateById(chapter);
    }

    private void ensureUniqueChapterNo(Long novelId, Integer chapterNo, Long excludeId) {
        Long count = chapterMapper.selectCount(new LambdaQueryWrapper<Chapter>()
                .eq(Chapter::getNovelId, novelId)
                .eq(Chapter::getChapterNo, chapterNo)
                .ne(excludeId != null, Chapter::getId, excludeId));
        if (count != null && count > 0) {
            throw new IllegalArgumentException("该小说下章节号已存在");
        }
    }
}
