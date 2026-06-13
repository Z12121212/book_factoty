package com.z.bookcreat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("novel_outline")
public class NovelOutline {

    private Long id;

    private Long novelId;

    private String outlineType;

    private Integer version;

    private String title;

    private String content;

    private Integer confirmed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
