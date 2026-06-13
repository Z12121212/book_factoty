package com.z.bookcreat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.z.bookcreat.dto.NovelCreateRequest;
import com.z.bookcreat.dto.NovelStatusRequest;
import com.z.bookcreat.dto.NovelUpdateRequest;
import com.z.bookcreat.entity.Genre;
import com.z.bookcreat.entity.Novel;
import com.z.bookcreat.mapper.NovelMapper;
import com.z.bookcreat.service.GenreService;
import com.z.bookcreat.service.NovelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class NovelServiceImpl implements NovelService {

    private static final Set<String> ALLOWED_STATUS = Set.of(
            "draft", "outlining", "writing", "paused", "completed", "archived"
    );

    private final NovelMapper novelMapper;
    private final GenreService genreService;

    @Override
    @Transactional
    public Long create(NovelCreateRequest request) {
        ensureEnabledGenre(request.getGenreId());

        LocalDateTime now = LocalDateTime.now();
        Novel novel = new Novel();
        novel.setUserId(request.getUserId());
        novel.setGenreId(request.getGenreId());
        novel.setTitle(request.getTitle());
        novel.setIdea(request.getIdea());
        novel.setStatus("draft");
        novel.setTargetWordCount(request.getTargetWordCount());
        novel.setWordsPerChapter(request.getWordsPerChapter() == null ? 2500 : request.getWordsPerChapter());
        novel.setCurrentChapterNo(0);
        novel.setWritingStyle(request.getWritingStyle());
        novel.setConfigJson(request.getConfigJson());
        novel.setCreatedAt(now);
        novel.setUpdatedAt(now);
        novelMapper.insert(novel);
        log.info("创建小说成功：{}", novel.getId());
        return novel.getId();
    }

    @Override
    @Transactional
    public void update(NovelUpdateRequest request) {
        getByIdRequired(request.getId());
        ensureEnabledGenre(request.getGenreId());

        Novel novel = new Novel();
        novel.setId(request.getId());
        novel.setGenreId(request.getGenreId());
        novel.setTitle(request.getTitle());
        novel.setIdea(request.getIdea());
        novel.setTargetWordCount(request.getTargetWordCount());
        novel.setWordsPerChapter(request.getWordsPerChapter());
        novel.setWritingStyle(request.getWritingStyle());
        novel.setConfigJson(request.getConfigJson());
        novel.setUpdatedAt(LocalDateTime.now());
        novelMapper.updateById(novel);
    }

    @Override
    @Transactional
    public void updateStatus(NovelStatusRequest request) {
        if (!ALLOWED_STATUS.contains(request.getStatus())) {
            throw new IllegalArgumentException("小说状态不合法");
        }
        getByIdRequired(request.getId());

        Novel novel = new Novel();
        novel.setId(request.getId());
        novel.setStatus(request.getStatus());
        novel.setUpdatedAt(LocalDateTime.now());
        novelMapper.updateById(novel);
    }

    @Override
    public Novel getByIdRequired(Long id) {
        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            throw new IllegalArgumentException("小说不存在");
        }
        return novel;
    }

    @Override
    public Page<Novel> page(long current, long size, Long userId, String status) {
        LambdaQueryWrapper<Novel> wrapper = new LambdaQueryWrapper<Novel>()
                .eq(userId != null, Novel::getUserId, userId)
                .eq(StringUtils.hasText(status), Novel::getStatus, status)
                .orderByDesc(Novel::getCreatedAt);
        return novelMapper.selectPage(new Page<>(current, size), wrapper);
    }

    private void ensureEnabledGenre(Long genreId) {
        Genre genre = genreService.getByIdRequired(genreId);
        if (!Integer.valueOf(1).equals(genre.getEnabled())) {
            throw new IllegalArgumentException("题材已禁用");
        }
    }
}
