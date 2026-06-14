package com.z.bookcreat.llm;

public interface LlmProviderAdapter {

    boolean supports(ModelRouteResult route);

    String chat(ModelRouteResult route, LlmChatRequest request);
}
