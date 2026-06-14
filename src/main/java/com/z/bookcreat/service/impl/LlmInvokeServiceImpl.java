package com.z.bookcreat.service.impl;

import com.z.bookcreat.llm.LlmChatRequest;
import com.z.bookcreat.llm.LlmChatResponse;
import com.z.bookcreat.llm.LlmProviderAdapter;
import com.z.bookcreat.llm.ModelRouteResult;
import com.z.bookcreat.service.LlmInvokeService;
import com.z.bookcreat.service.ModelRoutingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LlmInvokeServiceImpl implements LlmInvokeService {

    private final ModelRoutingService modelRoutingService;
    private final List<LlmProviderAdapter> providerAdapters;

    @Override
    public LlmChatResponse chat(LlmChatRequest request) {
        ModelRouteResult route = modelRoutingService.resolve(request.getNovelId(), request.getSceneCode());
        LlmProviderAdapter adapter = providerAdapters.stream()
                .filter(candidate -> candidate.supports(route))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("当前供应商没有可用调用适配器"));
        String content = adapter.chat(route, request);
        return LlmChatResponse.builder()
                .content(content)
                .route(route)
                .build();
    }
}
