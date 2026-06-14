# API V1

## Genre

Create genre:

```http
POST /api/genres
Content-Type: application/json

{
  "name": "末世",
  "description": "灾难、生存、秩序崩塌与重建",
  "promptHint": "强调生存压力、资源稀缺、社会秩序变化。"
}
```

Update genre:

```http
PUT /api/genres/{id}
Content-Type: application/json

{
  "name": "末世",
  "description": "灾难、生存、秩序崩塌与重建",
  "promptHint": "强调生存压力、资源稀缺、社会秩序变化。",
  "enabled": 1
}
```

Enable or disable genre:

```http
PATCH /api/genres/{id}/enabled?enabled=true
```

List enabled genres:

```http
GET /api/genres/enabled
```

Page genres:

```http
GET /api/genres?current=1&size=20&keyword=末世
```

## Novel

Create novel:

```http
POST /api/novels
Content-Type: application/json

{
  "userId": 1,
  "genreId": 1,
  "title": "雨亡之城",
  "idea": "主角是城市排水系统工程师，末世不是丧尸，而是雨水会复制死者记忆。",
  "targetWordCount": 2500000,
  "wordsPerChapter": 2500,
  "writingStyle": "悬疑、硬核生存、压迫感强",
  "configJson": "{}"
}
```

Update novel:

```http
PUT /api/novels/{id}
Content-Type: application/json

{
  "genreId": 1,
  "title": "雨亡之城",
  "idea": "主角是城市排水系统工程师，必须关闭会让亡者意识回流的地下泵站网络。",
  "targetWordCount": 2500000,
  "wordsPerChapter": 2500,
  "writingStyle": "悬疑、硬核生存、压迫感强",
  "configJson": "{}"
}
```

Update novel status:

```http
PATCH /api/novels/{id}/status
Content-Type: application/json

{
  "status": "paused"
}
```

Allowed novel status values:

```text
draft
outlining
writing
paused
completed
archived
```

Get novel detail:

```http
GET /api/novels/{id}
```

Page novels:

```http
GET /api/novels?current=1&size=20&userId=1&status=draft
```

## Outline

Create outline version:

```http
POST /api/outlines
Content-Type: application/json

{
  "novelId": 1,
  "outlineType": "global",
  "title": "全书大纲",
  "content": "第一卷：雨水开始复制死者记忆，主角发现地下泵站异常..."
}
```

Allowed outline type values:

```text
global
volume
chapter
ending
```

Update outline:

```http
PUT /api/outlines/{id}
Content-Type: application/json

{
  "title": "全书大纲",
  "content": "修改后的大纲内容..."
}
```

Confirm outline version:

```http
PATCH /api/outlines/{id}/confirm
```

Only one confirmed version is allowed for the same novel and outline type.

Get outline detail:

```http
GET /api/outlines/{id}
```

Get confirmed outline:

```http
GET /api/outlines/confirmed?novelId=1&outlineType=global
```

List outlines:

```http
GET /api/outlines?novelId=1&outlineType=global
```

## LLM Config

Create provider:

```http
POST /api/llm/providers
Content-Type: application/json

{
  "providerCode": "anthropic",
  "providerName": "Anthropic",
  "baseUrl": "https://api.anthropic.com",
  "apiKey": "sk-xxx"
}
```

List providers:

```http
GET /api/llm/providers
```

Create model:

```http
POST /api/llm/models
Content-Type: application/json

{
  "providerId": 1,
  "modelCode": "claude-sonnet-4",
  "modelName": "Claude Sonnet 4",
  "modelType": "chat",
  "contextWindow": 200000,
  "maxOutputTokens": 16000,
  "supportsStream": 1,
  "supportsJson": 1,
  "supportsTools": 1,
  "metadataJson": "{}"
}
```

For OpenAI-compatible relays, `metadataJson` can be used to specify protocol details, for example:

```json
{"wireApi":"responses"}
```

Allowed model type values:

```text
chat
embedding
```

Create scene:

```http
POST /api/llm/scenes
Content-Type: application/json

{
  "sceneCode": "chapter_generate",
  "sceneName": "章节生成",
  "description": "用于生成小说正文",
  "sceneType": "generate"
}
```

Allowed scene type values:

```text
generate
extract
review
embed
```

Bind scene and model:

```http
POST /api/llm/scene-models
Content-Type: application/json

{
  "sceneId": 1,
  "modelId": 1,
  "priority": 1,
  "roleType": "primary",
  "temperature": 0.9,
  "maxTokens": 4000,
  "topP": 0.9,
  "timeoutMs": 60000
}
```

Allowed role type values:

```text
primary
fallback
cheap
review
```

Set novel model override:

```http
POST /api/llm/novel-overrides
Content-Type: application/json

{
  "novelId": 1,
  "sceneId": 1,
  "modelId": 2,
  "temperature": 0.8,
  "maxTokens": 3500,
  "topP": 0.9,
  "timeoutMs": 60000,
  "enabled": 1
}
```

Resolve route:

```http
GET /api/llm/route?novelId=1&sceneCode=chapter_generate
```

Route priority:

```text
novel override
scene binding
```

## Idea Generate

Before calling this API, create an LLM scene with `sceneCode = idea_generate` and bind at least one enabled chat model.

Generate ideas:

```http
POST /api/ai/ideas/generate
Content-Type: application/json

{
  "novelId": 1,
  "genreName": "末世",
  "userIdea": "主角是城市排水系统工程师，末世不是丧尸，而是雨水会复制死者记忆。",
  "count": 3,
  "sceneCode": "idea_generate"
}
```

## Outline Generate

Before calling this API, create an LLM scene with `sceneCode = outline_generate` and bind at least one enabled chat model.

Generate outline draft:

```http
POST /api/ai/outlines/generate
Content-Type: application/json

{
  "novelId": 1,
  "outlineType": "global",
  "userInstruction": "更强调悬疑和节奏推进",
  "sceneCode": "outline_generate"
}
```
