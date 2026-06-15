package com.z.bookcreat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.bookcreat.dto.ChapterGenerateRequest;
import com.z.bookcreat.dto.ChapterGenerateResponse;
import com.z.bookcreat.entity.Chapter;
import com.z.bookcreat.entity.Genre;
import com.z.bookcreat.entity.Novel;
import com.z.bookcreat.entity.NovelOutline;
import com.z.bookcreat.llm.LlmChatRequest;
import com.z.bookcreat.llm.LlmChatResponse;
import com.z.bookcreat.service.ChapterGenerationService;
import com.z.bookcreat.service.ChapterService;
import com.z.bookcreat.service.GenreService;
import com.z.bookcreat.service.LlmInvokeService;
import com.z.bookcreat.service.NovelOutlineService;
import com.z.bookcreat.service.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterGenerationServiceImpl implements ChapterGenerationService {

    private final ChapterService chapterService;
    private final NovelService novelService;
    private final GenreService genreService;
    private final NovelOutlineService novelOutlineService;
    private final LlmInvokeService llmInvokeService;
    private final ObjectMapper objectMapper;

    @Override
    public ChapterGenerateResponse generate(ChapterGenerateRequest request) {
        Chapter chapter = chapterService.getByIdRequired(request.getChapterId());
        Novel novel = novelService.getByIdRequired(chapter.getNovelId());
        Genre genre = genreService.getByIdRequired(novel.getGenreId());

        LlmChatResponse response = llmInvokeService.chat(LlmChatRequest.builder()
                .novelId(chapter.getNovelId())
                .sceneCode(request.getSceneCode())
                .systemPrompt(buildSystemPrompt(novel))
                .userPrompt(buildUserPrompt(novel, genre, chapter))
                .build());

        String normalized = normalizeJson(response.getContent());
        JsonNode root = parseJson(normalized);
        String title = root.path("title").asText();
        String content = root.path("content").asText();
        chapterService.saveGeneratedContent(chapter.getId(), title, content);

        return ChapterGenerateResponse.builder()
                .chapterId(chapter.getId())
                .title(title)
                .content(content)
                .wordCount(content == null ? 0 : content.length())
                .route(response.getRoute())
                .rawContent(response.getContent())
                .build();
    }

    private String buildSystemPrompt(Novel novel) {
        int targetWords = novel.getWordsPerChapter() == null ? 2500 : novel.getWordsPerChapter();
        return """
                You are a Chinese web novel author writing polished chapter prose.
                Output must be valid JSON only. Do not output Markdown.
                JSON format:
                {"title":"chapter title","content":"chapter body"}
                Write in Simplified Chinese.
                The chapter body should be a real novel chapter, not an outline or summary.
                Target chapter length is about %d Chinese characters.
                Keep it readable, scene-driven, and suitable for long-form serialized fiction.
                """.formatted(targetWords);
    }

    private String buildUserPrompt(Novel novel, Genre genre, Chapter chapter) {
        String globalOutline = getConfirmedOutlineContent(chapter.getNovelId(), "global");
        String endingOutline = getConfirmedOutlineContent(chapter.getNovelId(), "ending");
        String chapterOutlines = joinOutlineContents(novelOutlineService.listByNovel(chapter.getNovelId(), "chapter"));
        String volumeOutlines = joinOutlineContents(novelOutlineService.listByNovel(chapter.getNovelId(), "volume"));

        return """
                Novel title: %s
                Genre: %s
                Writing style: %s
                Core idea: %s
                Chapter no: %s
                Draft chapter title: %s
                Chapter outline:
                %s

                Confirmed global outline:
                %s

                Confirmed ending setup:
                %s

                Existing volume outlines:
                %s

                Existing chapter outlines:
                %s

                Requirements:
                1. Write real narrative prose, not planning notes.
                2. Follow the chapter outline closely but keep the prose natural.
                3. Maintain long-form serialized pacing and suspense.
                4. Keep consistency with the confirmed global outline, ending, and chapter plan.
                5. End with a hook or forward pull when appropriate.
                """.formatted(
                nullSafe(novel.getTitle()),
                nullSafe(genre.getName()),
                nullSafe(novel.getWritingStyle()),
                nullSafe(novel.getIdea()),
                chapter.getChapterNo() == null ? "-" : chapter.getChapterNo(),
                nullSafe(chapter.getTitle()),
                nullSafe(chapter.getOutline()),
                emptyFallback(globalOutline),
                emptyFallback(endingOutline),
                emptyFallback(volumeOutlines),
                emptyFallback(chapterOutlines)
        );
    }

    private String getConfirmedOutlineContent(Long novelId, String outlineType) {
        try {
            NovelOutline outline = novelOutlineService.getConfirmed(novelId, outlineType);
            return outline == null ? "" : outline.getContent();
        } catch (Exception ignored) {
            return "";
        }
    }

    private String joinOutlineContents(List<NovelOutline> outlines) {
        if (outlines == null || outlines.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (NovelOutline outline : outlines) {
            if (!builder.isEmpty()) {
                builder.append("\n\n");
            }
            builder.append("Title: ").append(nullSafe(outline.getTitle())).append("\n");
            builder.append(outline.getContent() == null ? "" : outline.getContent());
        }
        return builder.toString();
    }

    private JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("模型返回不是合法 JSON: " + ex.getOriginalMessage());
        }
    }

    private String normalizeJson(String raw) {
        String trimmed = raw == null ? "" : raw.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```json", "")
                    .replaceFirst("^```", "")
                    .replaceFirst("```$", "")
                    .trim();
        }
        return trimmed;
    }

    private String nullSafe(String value) {
        return StringUtils.hasText(value) ? value : "-";
    }

    private String emptyFallback(String value) {
        return StringUtils.hasText(value) ? value : "None";
    }
}
