package com.z.bookcreat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("novel_model_override")
public class NovelModelOverride {

    private Long id;

    private Long novelId;

    private Long sceneId;

    private Long modelId;

    private BigDecimal temperature;

    private Integer maxTokens;

    private BigDecimal topP;

    private Integer timeoutMs;

    private Integer enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
