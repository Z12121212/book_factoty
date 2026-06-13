package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NovelStatusRequest {

    @NotNull(message = "小说ID不能为空")
    private Long id;

    @NotBlank(message = "小说状态不能为空")
    private String status;
}
