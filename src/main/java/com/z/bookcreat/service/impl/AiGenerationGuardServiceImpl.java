package com.z.bookcreat.service.impl;

import com.z.bookcreat.service.AiGenerationGuardService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class AiGenerationGuardServiceImpl implements AiGenerationGuardService {

    private final ConcurrentHashMap<String, AtomicBoolean> sceneLocks = new ConcurrentHashMap<>();

    @Override
    public void acquire(String sceneCode) {
        if (!StringUtils.hasText(sceneCode)) {
            throw new IllegalArgumentException("sceneCode 不能为空");
        }
        AtomicBoolean lock = sceneLocks.computeIfAbsent(sceneCode, key -> new AtomicBoolean(false));
        if (!lock.compareAndSet(false, true)) {
            throw new IllegalArgumentException("当前场景正在生成，请等待完成后再试");
        }
    }

    @Override
    public void release(String sceneCode) {
        AtomicBoolean lock = sceneLocks.get(sceneCode);
        if (lock != null) {
            lock.set(false);
        }
    }
}
