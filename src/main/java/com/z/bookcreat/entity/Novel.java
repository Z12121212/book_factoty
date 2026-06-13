package com.z.bookcreat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("novel")
public class Novel {

    private Long id;

    private Long userId;

    private Long genreId;

    private String title;

    private String idea;

    private String status;

    private Integer targetWordCount;

    private Integer wordsPerChapter;

    private Integer currentChapterNo;

    private String writingStyle;

    private String configJson;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
