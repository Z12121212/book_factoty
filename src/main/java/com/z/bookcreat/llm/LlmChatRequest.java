package com.z.bookcreat.llm;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LlmChatRequest {

    private Long novelId;

    private String sceneCode;

    private String systemPrompt;

    private String userPrompt;

    private BigDecimal temperatureOverride;

    private Integer maxTokensOverride;
}
