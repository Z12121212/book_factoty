package com.z.bookcreat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("llm_scene")
public class LlmScene {

    private Long id;

    private String sceneCode;

    private String sceneName;

    private String description;

    private String sceneType;

    private Integer enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
