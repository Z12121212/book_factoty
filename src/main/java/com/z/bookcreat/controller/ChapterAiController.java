package com.z.bookcreat.controller;

import com.z.bookcreat.common.ApiResponse;
import com.z.bookcreat.dto.ChapterGenerateRequest;
import com.z.bookcreat.dto.ChapterGenerateResponse;
import com.z.bookcreat.service.AiGenerationGuardService;
import com.z.bookcreat.service.ChapterGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/chapters")
public class ChapterAiController {

    private final ChapterGenerationService chapterGenerationService;
    private final AiGenerationGuardService aiGenerationGuardService;
    @Qualifier("aiTaskExecutor")
    private final Executor aiTaskExecutor;

    @PostMapping("/generate")
    public CompletableFuture<ApiResponse<ChapterGenerateResponse>> generate(@Valid @RequestBody ChapterGenerateRequest request) {
        aiGenerationGuardService.acquire(request.getSceneCode());
        return CompletableFuture.supplyAsync(() -> ApiResponse.ok(chapterGenerationService.generate(request)), aiTaskExecutor)
                .whenComplete((result, throwable) -> aiGenerationGuardService.release(request.getSceneCode()));
    }
}
