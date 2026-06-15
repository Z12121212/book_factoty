package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapterGenerateRequest {

    @NotNull(message = "章节ID不能为空")
    private Long chapterId;

    private String sceneCode = "chapter_generate";
}
