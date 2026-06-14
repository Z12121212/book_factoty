package com.z.bookcreat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("llm_model")
public class LlmModel {

    private Long id;

    private Long providerId;

    private String modelCode;

    private String modelName;

    private String modelType;

    private Integer contextWindow;

    private Integer maxOutputTokens;

    private Integer supportsStream;

    private Integer supportsJson;

    private Integer supportsTools;

    private Integer enabled;

    private String metadataJson;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
