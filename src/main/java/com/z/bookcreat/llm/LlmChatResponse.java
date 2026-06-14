package com.z.bookcreat.llm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LlmChatResponse {

    private String content;

    private ModelRouteResult route;
}
