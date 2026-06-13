CREATE TABLE genre (
  id BIGINT NOT NULL COMMENT '主键ID',
  name VARCHAR(64) NOT NULL COMMENT '题材名称，如末世、玄幻、恐怖',
  description VARCHAR(500) DEFAULT NULL COMMENT '题材说明',
  prompt_hint TEXT DEFAULT NULL COMMENT '该题材默认提示词补充信息',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0禁用，1启用',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_genre_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='小说题材表';

CREATE TABLE novel (
  id BIGINT NOT NULL COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  genre_id BIGINT NOT NULL COMMENT '题材ID',
  title VARCHAR(128) NOT NULL COMMENT '小说标题',
  idea TEXT NOT NULL COMMENT '用户确认后的核心创意',
  status VARCHAR(32) NOT NULL COMMENT '小说状态：draft/outlining/writing/paused/completed/archived',
  target_word_count INT DEFAULT NULL COMMENT '目标总字数',
  words_per_chapter INT NOT NULL DEFAULT 2500 COMMENT '每章目标字数',
  current_chapter_no INT NOT NULL DEFAULT 0 COMMENT '当前已完成或正在处理的章节序号',
  writing_style TEXT DEFAULT NULL COMMENT '写作风格要求',
  config_json JSON DEFAULT NULL COMMENT '小说级生成配置，如节奏、视角、限制规则',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user_status (user_id, status),
  KEY idx_genre_id (genre_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='小说项目表';

CREATE TABLE novel_outline (
  id BIGINT NOT NULL COMMENT '主键ID',
  novel_id BIGINT NOT NULL COMMENT '小说ID',
  outline_type VARCHAR(32) NOT NULL COMMENT '大纲类型：global全书/volume分卷/chapter章节/ending结局',
  version INT NOT NULL COMMENT '大纲版本号',
  title VARCHAR(128) DEFAULT NULL COMMENT '大纲标题',
  content LONGTEXT NOT NULL COMMENT '大纲正文',
  confirmed TINYINT NOT NULL DEFAULT 0 COMMENT '是否为用户确认版本：0否，1是',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_novel_type_confirmed (novel_id, outline_type, confirmed),
  KEY idx_novel_version (novel_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='小说大纲表';

CREATE TABLE novel_volume (
  id BIGINT NOT NULL COMMENT '主键ID',
  novel_id BIGINT NOT NULL COMMENT '小说ID',
  volume_no INT NOT NULL COMMENT '分卷序号',
  title VARCHAR(128) NOT NULL COMMENT '分卷标题',
  summary TEXT DEFAULT NULL COMMENT '分卷摘要',
  start_chapter_no INT DEFAULT NULL COMMENT '分卷起始章节号',
  end_chapter_no INT DEFAULT NULL COMMENT '分卷结束章节号',
  status VARCHAR(32) NOT NULL COMMENT '分卷状态：planned/writing/completed',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_novel_volume_no (novel_id, volume_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='小说分卷表';

CREATE TABLE chapter (
  id BIGINT NOT NULL COMMENT '主键ID',
  novel_id BIGINT NOT NULL COMMENT '小说ID',
  volume_id BIGINT DEFAULT NULL COMMENT '分卷ID',
  chapter_no INT NOT NULL COMMENT '章节序号',
  title VARCHAR(128) DEFAULT NULL COMMENT '章节标题',
  outline TEXT DEFAULT NULL COMMENT '本章大纲',
  content LONGTEXT DEFAULT NULL COMMENT '章节正文',
  summary TEXT DEFAULT NULL COMMENT '章节摘要',
  word_count INT NOT NULL DEFAULT 0 COMMENT '章节字数',
  status VARCHAR(32) NOT NULL COMMENT '章节状态：pending/generating/paused/completed/failed',
  generated_job_id BIGINT DEFAULT NULL COMMENT '生成该章节的任务ID',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_novel_chapter_no (novel_id, chapter_no),
  KEY idx_novel_status (novel_id, status),
  KEY idx_volume_id (volume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='小说章节表';

CREATE TABLE chapter_draft (
  id BIGINT NOT NULL COMMENT '主键ID',
  novel_id BIGINT NOT NULL COMMENT '小说ID',
  chapter_id BIGINT DEFAULT NULL COMMENT '章节ID，章节尚未创建时可为空',
  job_id BIGINT NOT NULL COMMENT '生成任务ID',
  chapter_no INT NOT NULL COMMENT '章节序号',
  content LONGTEXT DEFAULT NULL COMMENT '生成中的草稿内容',
  word_count INT NOT NULL DEFAULT 0 COMMENT '草稿字数',
  status VARCHAR(32) NOT NULL COMMENT '草稿状态：writing/paused/completed/abandoned',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_job_id (job_id),
  KEY idx_novel_chapter (novel_id, chapter_no),
  KEY idx_chapter_id (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='章节草稿表';

CREATE TABLE character_profile (
  id BIGINT NOT NULL COMMENT '主键ID',
  novel_id BIGINT NOT NULL COMMENT '小说ID',
  name VARCHAR(64) NOT NULL COMMENT '人物名称',
  role_type VARCHAR(32) DEFAULT NULL COMMENT '人物类型：protagonist/supporting/antagonist/passenger',
  profile TEXT DEFAULT NULL COMMENT '人物基础设定',
  motivation TEXT DEFAULT NULL COMMENT '人物动机',
  relationship_json JSON DEFAULT NULL COMMENT '人物关系配置',
  status VARCHAR(32) NOT NULL DEFAULT 'active' COMMENT '人物状态：active/dead/missing/hidden',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_novel_name (novel_id, name),
  KEY idx_novel_role (novel_id, role_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人物基础设定表';

CREATE TABLE character_state (
  id BIGINT NOT NULL COMMENT '主键ID',
  novel_id BIGINT NOT NULL COMMENT '小说ID',
  character_id BIGINT NOT NULL COMMENT '人物ID',
  chapter_no INT NOT NULL COMMENT '状态对应的章节号',
  physical_state TEXT DEFAULT NULL COMMENT '身体状态',
  mental_state TEXT DEFAULT NULL COMMENT '心理状态',
  location VARCHAR(128) DEFAULT NULL COMMENT '当前位置',
  relationship_change TEXT DEFAULT NULL COMMENT '关系变化',
  ability_state TEXT DEFAULT NULL COMMENT '能力或技能状态',
  summary TEXT DEFAULT NULL COMMENT '人物阶段状态摘要',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_character_chapter (character_id, chapter_no),
  KEY idx_novel_chapter (novel_id, chapter_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人物章节状态表';

CREATE TABLE foreshadowing (
  id BIGINT NOT NULL COMMENT '主键ID',
  novel_id BIGINT NOT NULL COMMENT '小说ID',
  introduced_chapter_id BIGINT DEFAULT NULL COMMENT '埋下伏笔的章节ID',
  resolved_chapter_id BIGINT DEFAULT NULL COMMENT '回收伏笔的章节ID',
  title VARCHAR(128) NOT NULL COMMENT '伏笔标题',
  content TEXT NOT NULL COMMENT '伏笔内容',
  importance INT NOT NULL DEFAULT 3 COMMENT '重要程度：1低，5高',
  status VARCHAR(32) NOT NULL COMMENT '伏笔状态：open/progressing/resolved/abandoned',
  planned_resolution TEXT DEFAULT NULL COMMENT '计划回收方式',
  actual_resolution TEXT DEFAULT NULL COMMENT '实际回收方式',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_novel_status_importance (novel_id, status, importance),
  KEY idx_intro_chapter (introduced_chapter_id),
  KEY idx_resolved_chapter (resolved_chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='伏笔管理表';

CREATE TABLE memory_entry (
  id BIGINT NOT NULL COMMENT '主键ID',
  novel_id BIGINT NOT NULL COMMENT '小说ID',
  source_type VARCHAR(32) NOT NULL COMMENT '来源类型：chapter/outline/character/foreshadowing/world_setting/user_note',
  source_id BIGINT DEFAULT NULL COMMENT '来源业务ID',
  memory_type VARCHAR(32) NOT NULL COMMENT '记忆类型：plot_event/character_state/world_rule/location/foreshadowing/style/relationship/object',
  title VARCHAR(128) DEFAULT NULL COMMENT '记忆标题',
  content TEXT NOT NULL COMMENT '记忆内容，用于向量化和上下文拼装',
  summary TEXT DEFAULT NULL COMMENT '记忆摘要',
  chapter_no INT DEFAULT NULL COMMENT '关联章节号',
  importance INT NOT NULL DEFAULT 3 COMMENT '重要程度：1低，5高',
  status VARCHAR(32) NOT NULL DEFAULT 'active' COMMENT '记忆状态：active/inactive/deleted',
  embedding_status VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '向量化状态：pending/processing/completed/failed/skipped',
  embedding_model VARCHAR(128) DEFAULT NULL COMMENT '向量模型名称',
  vector_collection VARCHAR(128) DEFAULT NULL COMMENT '向量库集合名称',
  vector_id VARCHAR(128) DEFAULT NULL COMMENT '向量库记录ID，建议使用memory_entry.id',
  content_hash VARCHAR(64) DEFAULT NULL COMMENT '记忆内容哈希，用于判断是否需要重新向量化',
  metadata_json JSON DEFAULT NULL COMMENT '扩展元数据',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_novel_type_status (novel_id, memory_type, status),
  KEY idx_embedding_status (embedding_status),
  KEY idx_vector_id (vector_id),
  KEY idx_source (source_type, source_id),
  KEY idx_novel_chapter (novel_id, chapter_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='长期记忆表，作为MySQL与向量数据库的桥接表';

CREATE TABLE generation_job (
  id BIGINT NOT NULL COMMENT '主键ID',
  novel_id BIGINT NOT NULL COMMENT '小说ID',
  job_type VARCHAR(32) NOT NULL COMMENT '任务类型：idea/outline/chapter/summary/foreshadowing/embedding/review',
  status VARCHAR(32) NOT NULL COMMENT '任务状态：pending/running/pause_requested/paused/completed/failed/cancelled',
  request_json JSON DEFAULT NULL COMMENT '任务请求参数快照',
  result_json JSON DEFAULT NULL COMMENT '任务结果摘要',
  current_chapter_no INT DEFAULT NULL COMMENT '当前处理章节号',
  total_chapters INT DEFAULT NULL COMMENT '本任务计划处理章节数',
  error_message TEXT DEFAULT NULL COMMENT '失败原因',
  started_at DATETIME DEFAULT NULL COMMENT '开始时间',
  finished_at DATETIME DEFAULT NULL COMMENT '结束时间',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_novel_status (novel_id, status),
  KEY idx_job_type_status (job_type, status),
  KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='生成任务表';

CREATE TABLE generation_event (
  id BIGINT NOT NULL COMMENT '主键ID',
  job_id BIGINT NOT NULL COMMENT '生成任务ID',
  event_type VARCHAR(32) NOT NULL COMMENT '事件类型：created/started/chunk_saved/paused/resumed/completed/failed',
  message TEXT DEFAULT NULL COMMENT '事件描述',
  payload_json JSON DEFAULT NULL COMMENT '事件扩展数据',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_job_id (job_id),
  KEY idx_event_type (event_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='生成任务事件表';

CREATE TABLE prompt_template (
  id BIGINT NOT NULL COMMENT '主键ID',
  template_code VARCHAR(64) NOT NULL COMMENT '模板编码',
  name VARCHAR(128) NOT NULL COMMENT '模板名称',
  template_type VARCHAR(32) NOT NULL COMMENT '模板类型：idea/outline/chapter/summary/foreshadowing/review/embedding_query',
  content LONGTEXT NOT NULL COMMENT '提示词模板内容',
  version INT NOT NULL COMMENT '模板版本号',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0禁用，1启用',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_template_code_version (template_code, version),
  KEY idx_type_enabled (template_type, enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提示词模板表';

CREATE TABLE model_call_log (
  id BIGINT NOT NULL COMMENT '主键ID',
  novel_id BIGINT DEFAULT NULL COMMENT '小说ID',
  job_id BIGINT DEFAULT NULL COMMENT '生成任务ID',
  call_type VARCHAR(64) NOT NULL COMMENT '调用类型：generate_idea/generate_outline/generate_chapter/summarize/extract_foreshadowing/embed/review',
  model_name VARCHAR(128) NOT NULL COMMENT '模型名称',
  prompt_tokens INT DEFAULT NULL COMMENT '输入token数量',
  completion_tokens INT DEFAULT NULL COMMENT '输出token数量',
  total_tokens INT DEFAULT NULL COMMENT '总token数量',
  latency_ms INT DEFAULT NULL COMMENT '调用耗时毫秒',
  success TINYINT NOT NULL COMMENT '是否成功：0失败，1成功',
  error_message TEXT DEFAULT NULL COMMENT '错误信息',
  request_hash VARCHAR(64) DEFAULT NULL COMMENT '请求内容哈希，用于排查重复调用',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_novel_call_type (novel_id, call_type),
  KEY idx_job_id (job_id),
  KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='大模型调用日志表';

