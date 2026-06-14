package com.z.bookcreat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.bookcreat.dto.OutlineGenerateRequest;
import com.z.bookcreat.dto.OutlineGenerateResponse;
import com.z.bookcreat.entity.Genre;
import com.z.bookcreat.entity.Novel;
import com.z.bookcreat.entity.NovelOutline;
import com.z.bookcreat.llm.LlmChatRequest;
import com.z.bookcreat.llm.LlmChatResponse;
import com.z.bookcreat.service.GenreService;
import com.z.bookcreat.service.LlmInvokeService;
import com.z.bookcreat.service.NovelOutlineService;
import com.z.bookcreat.service.NovelService;
import com.z.bookcreat.service.OutlineGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutlineGenerationServiceImpl implements OutlineGenerationService {

    private final NovelService novelService;
    private final GenreService genreService;
    private final NovelOutlineService novelOutlineService;
    private final LlmInvokeService llmInvokeService;
    private final ObjectMapper objectMapper;

    @Override
    public OutlineGenerateResponse generate(OutlineGenerateRequest request) {
        Novel novel = novelService.getByIdRequired(request.getNovelId());
        Genre genre = genreService.getByIdRequired(novel.getGenreId());

        LlmChatResponse response = llmInvokeService.chat(LlmChatRequest.builder()
                .novelId(request.getNovelId())
                .sceneCode(request.getSceneCode())
                .systemPrompt(buildSystemPrompt(request))
                .userPrompt(buildUserPrompt(novel, genre, request))
                .build());

        String normalized = normalizeJson(response.getContent());
        JsonNode root = parseJson(normalized);
        return OutlineGenerateResponse.builder()
                .title(root.path("title").asText())
                .content(root.path("content").asText())
                .route(response.getRoute())
                .rawContent(response.getContent())
                .build();
    }

    private String buildSystemPrompt(OutlineGenerateRequest request) {
        String chapterSpecificRules = "";
        if ("chapter".equals(request.getOutlineType())) {
            chapterSpecificRules = """
                    Chapter outline specific rules:
                    1. Treat the requested chapter range as a true serialized chapter span, not as a compressed synopsis.
                    2. Keep pacing slow enough for long-form web novel serialization.
                    3. Each chapter should advance only a small and reasonable amount of plot.
                    4. Do not finish a whole major dungeon, whole major arc, or multiple major events inside a tiny chapter range.
                    5. Early chapter ranges should focus on setup, conflict build-up, atmosphere, investigation, preparation, trial, and incremental reveals.
                    6. The larger the planned chapter count of the volume, the more carefully the progression must be distributed.
                    7. If the requested range is short, output only local progression and leave room for later chapter ranges.
                    """;
        }
        return """
                You are a senior Chinese web novel planning editor.
                Generate a %s for a long-form serialized novel.
                Output must be valid JSON only. Do not output Markdown.
                JSON format:
                {"title":"outline title","content":"detailed outline content"}
                The content must be written in Simplified Chinese.
                The outline must be practical, structured, and directly usable for later chapter writing.
                %s
                """.formatted(request.getOutlineType(), chapterSpecificRules);
    }

    private String buildUserPrompt(Novel novel, Genre genre, OutlineGenerateRequest request) {
        String globalOutline = getConfirmedOutlineContent(request.getNovelId(), "global");
        String endingOutline = getConfirmedOutlineContent(request.getNovelId(), "ending");
        String volumeOutlines = joinOutlineContents(novelOutlineService.listByNovel(request.getNovelId(), "volume"));
        String extra = StringUtils.hasText(request.getUserInstruction()) ? request.getUserInstruction() : "None";

        return """
                Novel title: %s
                Genre: %s
                Core idea: %s
                Target total words: %s
                Words per chapter: %s
                Writing style: %s
                Outline type: %s
                Volume count hint: %s
                Chapters per volume hint: %s
                Target volume no: %s
                Chapter range: %s - %s
                Extra instruction: %s

                Confirmed global outline:
                %s

                Confirmed ending setup:
                %s

                Existing volume outlines:
                %s

                Requirements by outline type:
                1. global: produce a full-book structured blueprint.
                2. volume: split the full story into clear volumes with goals, conflicts, chapter ranges, and volume-end hooks.
                3. chapter: generate actionable chapter-by-chapter plan for the specified volume or chapter range.
                4. ending: focus on final conflict, resolution, fate of major characters, and payoff of major foreshadowing.

                General requirements:
                1. Suitable for Chinese long web novel serialization.
                2. Strong progression, conflict escalation, and clear hooks.
                3. Must stay consistent with confirmed global outline and ending.
                4. If some reference is missing, still produce a usable result and clearly organize it.
                5. When outline type is chapter, the requested chapter range must respect long-form pacing and cannot rush through the volume's major content too quickly.
                """.formatted(
                nullSafe(novel.getTitle()),
                nullSafe(genre.getName()),
                nullSafe(novel.getIdea()),
                novel.getTargetWordCount() == null ? "-" : novel.getTargetWordCount(),
                novel.getWordsPerChapter() == null ? "-" : novel.getWordsPerChapter(),
                nullSafe(novel.getWritingStyle()),
                request.getOutlineType(),
                request.getVolumeCount() == null ? "-" : request.getVolumeCount(),
                request.getChaptersPerVolume() == null ? "-" : request.getChaptersPerVolume(),
                request.getVolumeNo() == null ? "-" : request.getVolumeNo(),
                request.getStartChapterNo() == null ? "-" : request.getStartChapterNo(),
                request.getEndChapterNo() == null ? "-" : request.getEndChapterNo(),
                extra,
                emptyFallback(globalOutline),
                emptyFallback(endingOutline),
                emptyFallback(volumeOutlines)
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
