CREATE TABLE llm_provider (
  id BIGINT NOT NULL COMMENT 'Primary key',
  provider_code VARCHAR(64) NOT NULL COMMENT 'Provider code, such as openai/anthropic/deepseek',
  provider_name VARCHAR(128) NOT NULL COMMENT 'Provider display name',
  base_url VARCHAR(512) NOT NULL COMMENT 'Provider base url',
  api_key VARCHAR(512) NOT NULL COMMENT 'Provider api key, plaintext for v1',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT 'Enabled flag: 0 disabled, 1 enabled',
  created_at DATETIME NOT NULL COMMENT 'Created time',
  updated_at DATETIME NOT NULL COMMENT 'Updated time',
  PRIMARY KEY (id),
  UNIQUE KEY uk_provider_code (provider_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='LLM provider config';

CREATE TABLE llm_model (
  id BIGINT NOT NULL COMMENT 'Primary key',
  provider_id BIGINT NOT NULL COMMENT 'Provider id',
  model_code VARCHAR(128) NOT NULL COMMENT 'Model code',
  model_name VARCHAR(128) NOT NULL COMMENT 'Model display name',
  model_type VARCHAR(32) NOT NULL COMMENT 'Model type: chat/embedding',
  context_window INT DEFAULT NULL COMMENT 'Context window tokens',
  max_output_tokens INT DEFAULT NULL COMMENT 'Max output tokens',
  supports_stream TINYINT NOT NULL DEFAULT 0 COMMENT 'Supports stream output',
  supports_json TINYINT NOT NULL DEFAULT 0 COMMENT 'Supports json output',
  supports_tools TINYINT NOT NULL DEFAULT 0 COMMENT 'Supports tool calling',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT 'Enabled flag: 0 disabled, 1 enabled',
  metadata_json JSON DEFAULT NULL COMMENT 'Extension metadata',
  created_at DATETIME NOT NULL COMMENT 'Created time',
  updated_at DATETIME NOT NULL COMMENT 'Updated time',
  PRIMARY KEY (id),
  UNIQUE KEY uk_provider_model_code (provider_id, model_code),
  KEY idx_model_type_enabled (model_type, enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='LLM model config';

CREATE TABLE llm_scene (
  id BIGINT NOT NULL COMMENT 'Primary key',
  scene_code VARCHAR(64) NOT NULL COMMENT 'Scene code, such as chapter_generate',
  scene_name VARCHAR(128) NOT NULL COMMENT 'Scene display name',
  description VARCHAR(500) DEFAULT NULL COMMENT 'Scene description',
  scene_type VARCHAR(32) NOT NULL COMMENT 'Scene type: generate/extract/review/embed',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT 'Enabled flag: 0 disabled, 1 enabled',
  created_at DATETIME NOT NULL COMMENT 'Created time',
  updated_at DATETIME NOT NULL COMMENT 'Updated time',
  PRIMARY KEY (id),
  UNIQUE KEY uk_scene_code (scene_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='LLM scene config';

CREATE TABLE llm_scene_model (
  id BIGINT NOT NULL COMMENT 'Primary key',
  scene_id BIGINT NOT NULL COMMENT 'Scene id',
  model_id BIGINT NOT NULL COMMENT 'Model id',
  priority INT NOT NULL DEFAULT 1 COMMENT 'Priority, lower value means higher priority',
  role_type VARCHAR(32) NOT NULL COMMENT 'Role type: primary/fallback/cheap/review',
  temperature DECIMAL(4,2) DEFAULT NULL COMMENT 'Temperature override',
  max_tokens INT DEFAULT NULL COMMENT 'Max tokens override',
  top_p DECIMAL(4,2) DEFAULT NULL COMMENT 'Top p override',
  timeout_ms INT DEFAULT NULL COMMENT 'Timeout override in milliseconds',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT 'Enabled flag: 0 disabled, 1 enabled',
  created_at DATETIME NOT NULL COMMENT 'Created time',
  updated_at DATETIME NOT NULL COMMENT 'Updated time',
  PRIMARY KEY (id),
  UNIQUE KEY uk_scene_model_role (scene_id, model_id, role_type),
  KEY idx_scene_priority_enabled (scene_id, priority, enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='LLM scene to model binding';

CREATE TABLE novel_model_override (
  id BIGINT NOT NULL COMMENT 'Primary key',
  novel_id BIGINT NOT NULL COMMENT 'Novel id',
  scene_id BIGINT NOT NULL COMMENT 'Scene id',
  model_id BIGINT NOT NULL COMMENT 'Model id',
  temperature DECIMAL(4,2) DEFAULT NULL COMMENT 'Temperature override',
  max_tokens INT DEFAULT NULL COMMENT 'Max tokens override',
  top_p DECIMAL(4,2) DEFAULT NULL COMMENT 'Top p override',
  timeout_ms INT DEFAULT NULL COMMENT 'Timeout override in milliseconds',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT 'Enabled flag: 0 disabled, 1 enabled',
  created_at DATETIME NOT NULL COMMENT 'Created time',
  updated_at DATETIME NOT NULL COMMENT 'Updated time',
  PRIMARY KEY (id),
  UNIQUE KEY uk_novel_scene (novel_id, scene_id),
  KEY idx_model_enabled (model_id, enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Novel level model override';
