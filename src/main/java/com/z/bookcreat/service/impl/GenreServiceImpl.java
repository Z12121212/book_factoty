package com.z.bookcreat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.z.bookcreat.dto.GenreCreateRequest;
import com.z.bookcreat.dto.GenreUpdateRequest;
import com.z.bookcreat.entity.Genre;
import com.z.bookcreat.mapper.GenreMapper;
import com.z.bookcreat.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreMapper genreMapper;

    @Override
    @Transactional
    public Long create(GenreCreateRequest request) {
        ensureNameUnique(request.getName(), null);

        LocalDateTime now = LocalDateTime.now();
        Genre genre = new Genre();
        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        genre.setPromptHint(request.getPromptHint());
        genre.setEnabled(1);
        genre.setCreatedAt(now);
        genre.setUpdatedAt(now);
        genreMapper.insert(genre);
        return genre.getId();
    }

    @Override
    @Transactional
    public void update(GenreUpdateRequest request) {
        getByIdRequired(request.getId());
        ensureNameUnique(request.getName(), request.getId());

        Genre genre = new Genre();
        genre.setId(request.getId());
        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        genre.setPromptHint(request.getPromptHint());
        genre.setEnabled(request.getEnabled());
        genre.setUpdatedAt(LocalDateTime.now());
        genreMapper.updateById(genre);
    }

    @Override
    @Transactional
    public void setEnabled(Long id, boolean enabled) {
        getByIdRequired(id);

        Genre genre = new Genre();
        genre.setId(id);
        genre.setEnabled(enabled ? 1 : 0);
        genre.setUpdatedAt(LocalDateTime.now());
        genreMapper.updateById(genre);
    }

    @Override
    public Genre getByIdRequired(Long id) {
        Genre genre = genreMapper.selectById(id);
        if (genre == null) {
            throw new IllegalArgumentException("题材不存在");
        }
        return genre;
    }

    @Override
    public List<Genre> listEnabled() {
        return genreMapper.selectList(new LambdaQueryWrapper<Genre>()
                .eq(Genre::getEnabled, 1)
                .orderByAsc(Genre::getName));
    }

    @Override
    public Page<Genre> page(long current, long size, String keyword) {
        LambdaQueryWrapper<Genre> wrapper = new LambdaQueryWrapper<Genre>()
                .like(StringUtils.hasText(keyword), Genre::getName, keyword)
                .orderByDesc(Genre::getCreatedAt);
        return genreMapper.selectPage(new Page<>(current, size), wrapper);
    }

    private void ensureNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<Genre> wrapper = new LambdaQueryWrapper<Genre>()
                .eq(Genre::getName, name)
                .ne(excludeId != null, Genre::getId, excludeId);
        if (genreMapper.selectCount(wrapper) > 0) {
            throw new IllegalArgumentException("题材名称已存在");
        }
    }
}
