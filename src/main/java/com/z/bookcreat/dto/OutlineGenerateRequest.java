package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OutlineGenerateRequest {

    @NotNull(message = "小说ID不能为空")
    private Long novelId;

    @NotBlank(message = "大纲类型不能为空")
    private String outlineType = "global";

    private Integer volumeCount;

    private Integer chaptersPerVolume;

    private Integer volumeNo;

    private Integer startChapterNo;

    private Integer endChapterNo;

    private String userInstruction;

    private String sceneCode = "outline_generate";
}
