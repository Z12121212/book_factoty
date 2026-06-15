package com.z.bookcreat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chapter")
public class Chapter {

    private Long id;

    private Long novelId;

    private Long volumeId;

    private Integer chapterNo;

    private String title;

    private String outline;

    private String content;

    private String summary;

    private Integer wordCount;

    private String status;

    private Long generatedJobId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
