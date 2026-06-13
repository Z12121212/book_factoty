package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenreCreateRequest {

    @NotBlank(message = "题材名称不能为空")
    @Size(max = 64, message = "题材名称不能超过64个字符")
    private String name;

    @Size(max = 500, message = "题材说明不能超过500个字符")
    private String description;

    private String promptHint;
}
