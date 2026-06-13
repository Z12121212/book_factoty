package com.z.bookcreat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.z.bookcreat.dto.NovelOutlineCreateRequest;
import com.z.bookcreat.dto.NovelOutlineUpdateRequest;
import com.z.bookcreat.entity.NovelOutline;
import com.z.bookcreat.mapper.NovelOutlineMapper;
import com.z.bookcreat.service.NovelOutlineService;
import com.z.bookcreat.service.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NovelOutlineServiceImpl implements NovelOutlineService {

    private static final Set<String> ALLOWED_TYPES = Set.of("global", "volume", "chapter", "ending");

    private final NovelOutlineMapper novelOutlineMapper;
    private final NovelService novelService;

    @Override
    @Transactional
    public Long createVersion(NovelOutlineCreateRequest request) {
        validateOutlineType(request.getOutlineType());
        novelService.getByIdRequired(request.getNovelId());

        LocalDateTime now = LocalDateTime.now();
        NovelOutline outline = new NovelOutline();
        outline.setNovelId(request.getNovelId());
        outline.setOutlineType(request.getOutlineType());
        outline.setVersion(nextVersion(request.getNovelId(), request.getOutlineType()));
        outline.setTitle(request.getTitle());
        outline.setContent(request.getContent());
        outline.setConfirmed(0);
        outline.setCreatedAt(now);
        outline.setUpdatedAt(now);
        novelOutlineMapper.insert(outline);
        return outline.getId();
    }

    @Override
    @Transactional
    public void update(NovelOutlineUpdateRequest request) {
        getByIdRequired(request.getId());

        NovelOutline outline = new NovelOutline();
        outline.setId(request.getId());
        outline.setTitle(request.getTitle());
        outline.setContent(request.getContent());
        outline.setUpdatedAt(LocalDateTime.now());
        novelOutlineMapper.updateById(outline);
    }

    @Override
    @Transactional
    public void confirm(Long id) {
        NovelOutline outline = getByIdRequired(id);
        LocalDateTime now = LocalDateTime.now();

        novelOutlineMapper.update(null, new LambdaUpdateWrapper<NovelOutline>()
                .eq(NovelOutline::getNovelId, outline.getNovelId())
                .eq(NovelOutline::getOutlineType, outline.getOutlineType())
                .set(NovelOutline::getConfirmed, 0)
                .set(NovelOutline::getUpdatedAt, now));

        NovelOutline confirmed = new NovelOutline();
        confirmed.setId(id);
        confirmed.setConfirmed(1);
        confirmed.setUpdatedAt(now);
        novelOutlineMapper.updateById(confirmed);
    }

    @Override
    public NovelOutline getByIdRequired(Long id) {
        NovelOutline outline = novelOutlineMapper.selectById(id);
        if (outline == null) {
            throw new IllegalArgumentException("大纲不存在");
        }
        return outline;
    }

    @Override
    public NovelOutline getConfirmed(Long novelId, String outlineType) {
        validateOutlineType(outlineType);
        novelService.getByIdRequired(novelId);

        NovelOutline outline = novelOutlineMapper.selectOne(new LambdaQueryWrapper<NovelOutline>()
                .eq(NovelOutline::getNovelId, novelId)
                .eq(NovelOutline::getOutlineType, outlineType)
                .eq(NovelOutline::getConfirmed, 1)
                .last("LIMIT 1"));
        if (outline == null) {
            throw new IllegalArgumentException("确认版大纲不存在");
        }
        return outline;
    }

    @Override
    public List<NovelOutline> listByNovel(Long novelId, String outlineType) {
        novelService.getByIdRequired(novelId);
        if (StringUtils.hasText(outlineType)) {
            validateOutlineType(outlineType);
        }

        return novelOutlineMapper.selectList(new LambdaQueryWrapper<NovelOutline>()
                .eq(NovelOutline::getNovelId, novelId)
                .eq(StringUtils.hasText(outlineType), NovelOutline::getOutlineType, outlineType)
                .orderByAsc(NovelOutline::getOutlineType)
                .orderByDesc(NovelOutline::getVersion));
    }

    private Integer nextVersion(Long novelId, String outlineType) {
        NovelOutline latest = novelOutlineMapper.selectOne(new LambdaQueryWrapper<NovelOutline>()
                .eq(NovelOutline::getNovelId, novelId)
                .eq(NovelOutline::getOutlineType, outlineType)
                .orderByDesc(NovelOutline::getVersion)
                .last("LIMIT 1"));
        return latest == null ? 1 : latest.getVersion() + 1;
    }

    private void validateOutlineType(String outlineType) {
        if (!ALLOWED_TYPES.contains(outlineType)) {
            throw new IllegalArgumentException("大纲类型不合法");
        }
    }
}
