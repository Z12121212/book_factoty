package com.z.bookcreat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("genre")
public class Genre {

    private Long id;

    private String name;

    private String description;

    private String promptHint;

    private Integer enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
