package com.z.bookcreat.controller;

import com.z.bookcreat.common.ApiResponse;
import com.z.bookcreat.dto.OutlineGenerateRequest;
import com.z.bookcreat.dto.OutlineGenerateResponse;
import com.z.bookcreat.service.AiGenerationGuardService;
import com.z.bookcreat.service.OutlineGenerationService;
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
@RequestMapping("/api/ai/outlines")
public class OutlineAiController {

    private final OutlineGenerationService outlineGenerationService;
    private final AiGenerationGuardService aiGenerationGuardService;
    @Qualifier("aiTaskExecutor")
    private final Executor aiTaskExecutor;

    @PostMapping("/generate")
    public CompletableFuture<ApiResponse<OutlineGenerateResponse>> generate(@Valid @RequestBody OutlineGenerateRequest request) {
        aiGenerationGuardService.acquire(request.getSceneCode());
        return CompletableFuture.supplyAsync(() -> ApiResponse.ok(outlineGenerationService.generate(request)), aiTaskExecutor)
                .whenComplete((result, throwable) -> aiGenerationGuardService.release(request.getSceneCode()));
    }
}
