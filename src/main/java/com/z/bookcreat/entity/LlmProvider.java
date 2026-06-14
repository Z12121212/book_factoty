package com.z.bookcreat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("llm_provider")
public class LlmProvider {

    private Long id;

    private String providerCode;

    private String providerName;

    private String baseUrl;

    private String apiKey;

    private Integer enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
