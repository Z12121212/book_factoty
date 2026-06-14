const panelMeta = {
    genres: { title: "题材管理", subtitle: "创建与维护小说题材。" },
    novels: { title: "小说管理", subtitle: "创建小说项目并查看当前状态。" },
    outlines: { title: "大纲管理", subtitle: "维护大纲版本并查询确认版内容。" },
    "llm-providers": { title: "LLM 供应商", subtitle: "维护中转站或官方供应商连接信息。" },
    "llm-models": { title: "LLM 模型", subtitle: "维护可调用模型与能力标签。" },
    "llm-scenes": { title: "LLM 场景", subtitle: "定义创意、正文、摘要等业务场景。" },
    "llm-bindings": { title: "场景绑定", subtitle: "为每个场景绑定主模型、备用模型和低成本模型。" },
    "llm-overrides": { title: "小说覆盖", subtitle: "给某本小说单独覆盖场景模型。" },
    "llm-route": { title: "路由测试", subtitle: "验证某个场景在当前配置下会命中哪个模型。" }
};

const referenceData = {
    genres: [],
    novels: [],
    providers: [],
    models: [],
    scenes: []
};

document.addEventListener("DOMContentLoaded", () => {
    setupNavigation();
    setupForms();
    applyOutlineModeVisibility();
    refreshGenres();
    refreshNovels();
    refreshProviders();
    refreshModels();
    refreshScenes();
});

function setupNavigation() {
    document.querySelectorAll(".nav-item").forEach((button) => {
        button.addEventListener("click", () => {
            document.querySelectorAll(".nav-item").forEach((item) => item.classList.remove("active"));
            document.querySelectorAll(".panel").forEach((panel) => panel.classList.remove("active"));
            button.classList.add("active");
            const name = button.dataset.panel;
            document.getElementById(`panel-${name}`).classList.add("active");
            document.getElementById("panel-title").textContent = panelMeta[name].title;
            document.getElementById("panel-subtitle").textContent = panelMeta[name].subtitle;
        });
    });
}

function setupForms() {
    bindSubmit("genre-form", async (form) => {
        await api("/api/genres", { method: "POST", body: formJson(form) });
        form.reset();
        toast("题材已创建");
        refreshGenres();
    });

    bindSubmit("novel-form", async (form) => {
        await api("/api/novels", {
            method: "POST",
            body: formJson(form, toNumberFields("targetWordCount", "wordsPerChapter"))
        });
        form.reset();
        form.elements.userId.value = "1";
        form.elements.targetWordCount.value = "2500000";
        form.elements.wordsPerChapter.value = "2500";
        form.elements.configJson.value = "{}";
        toast("小说已创建");
        refreshNovels();
    });

    bindSubmit("outline-form", async (form) => {
        await api("/api/outlines", { method: "POST", body: formJson(form) });
        toast("大纲版本已创建");
    });

    bindSubmit("outline-query-form", async (form) => {
        const data = formJson(form);
        const params = new URLSearchParams();
        params.set("novelId", data.novelId);
        if (data.outlineType) params.set("outlineType", data.outlineType);
        const result = await api(`/api/outlines?${params.toString()}`);
        renderOutlines(result.data || []);
    });

    bindSubmit("provider-form", async (form) => {
        await api("/api/llm/providers", { method: "POST", body: formJson(form) });
        form.reset();
        toast("供应商已创建");
        refreshProviders();
    });

    bindSubmit("model-form", async (form) => {
        await api("/api/llm/models", {
            method: "POST",
            body: formJson(form, toNumberFields("contextWindow", "maxOutputTokens", "supportsStream", "supportsJson", "supportsTools"))
        });
        toast("模型已创建");
        refreshModels();
    });

    bindSubmit("scene-form", async (form) => {
        await api("/api/llm/scenes", { method: "POST", body: formJson(form) });
        form.reset();
        toast("场景已创建");
        refreshScenes();
    });

    bindSubmit("binding-form", async (form) => {
        await api("/api/llm/scene-models", {
            method: "POST",
            body: formJson(form, toNumberFields("priority", "maxTokens", "timeoutMs"), toDecimalFields("temperature", "topP"))
        });
        toast("场景模型绑定已保存");
    });

    bindSubmit("binding-query-form", async (form) => {
        const data = formJson(form);
        const result = await api(`/api/llm/scene-models?sceneId=${encodeURIComponent(data.sceneId)}`);
        renderBindings(result.data || []);
    });

    bindSubmit("override-form", async (form) => {
        await api("/api/llm/novel-overrides", {
            method: "POST",
            body: formJson(form, toNumberFields("maxTokens", "timeoutMs", "enabled"), toDecimalFields("temperature", "topP"))
        });
        toast("小说覆盖已保存");
    });

    bindSubmit("override-query-form", async (form) => {
        const data = formJson(form);
        const result = await api(`/api/llm/novel-overrides?novelId=${encodeURIComponent(data.novelId)}`);
        renderOverrides(result.data || []);
    });

    bindSubmit("route-form", async (form) => {
        const raw = formJson(form);
        const params = new URLSearchParams();
        if (raw.novelId) params.set("novelId", raw.novelId);
        params.set("sceneCode", raw.sceneCode);
        const result = await api(`/api/llm/route?${params.toString()}`);
        document.getElementById("route-result").textContent = JSON.stringify(result.data, null, 2);
    });

    document.getElementById("generate-ideas-btn")?.addEventListener("click", generateIdeas);
    document.getElementById("generate-outline-btn")?.addEventListener("click", generateOutline);
    document.getElementById("outline-mode")?.addEventListener("change", (event) => {
        const outlineForm = document.getElementById("outline-form");
        outlineForm.elements.outlineType.value = event.target.value;
        applyOutlineModeVisibility();
    });

    document.querySelector("[data-action='refresh-genres']")?.addEventListener("click", refreshGenres);
    document.querySelector("[data-action='refresh-novels']")?.addEventListener("click", refreshNovels);
    document.querySelector("[data-action='refresh-providers']")?.addEventListener("click", refreshProviders);
    document.querySelector("[data-action='refresh-models']")?.addEventListener("click", refreshModels);
    document.querySelector("[data-action='refresh-scenes']")?.addEventListener("click", refreshScenes);
}

function applyOutlineModeVisibility() {
    const mode = document.getElementById("outline-mode")?.value || "global";
    const showVolume = mode === "volume";
    const showChapter = mode === "chapter";

    document.querySelectorAll(".outline-option.mode-volume").forEach((element) => {
        element.classList.toggle("is-hidden", !showVolume);
    });
    document.querySelectorAll(".outline-option.mode-chapter").forEach((element) => {
        element.classList.toggle("is-hidden", !showChapter);
    });
}

async function refreshGenres() {
    const result = await api("/api/genres?current=1&size=50");
    const records = result.data?.records || [];
    referenceData.genres = records;
    populateReferenceSelects();
    const container = document.getElementById("genres-list");
    renderList(container, records, (item) => `
        <div class="list-item-title">${escapeHtml(item.name)} <span class="badge">${item.enabled === 1 ? "enabled" : "disabled"}</span></div>
        <div class="list-item-meta">
            <span>ID: ${item.id}</span>
            <span>更新时间: ${item.updatedAt || "-"}</span>
        </div>
        <div class="list-item-content">${escapeHtml(item.description || "")}</div>
        <div class="list-item-content">${escapeHtml(item.promptHint || "")}</div>
    `);
}

async function refreshNovels() {
    const result = await api("/api/novels?current=1&size=50");
    const records = result.data?.records || [];
    referenceData.novels = records;
    populateReferenceSelects();
    const container = document.getElementById("novels-list");
    renderList(container, records, (item) => `
        <div class="list-item-title">${escapeHtml(item.title)} <span class="badge">${escapeHtml(item.status)}</span></div>
        <div class="list-item-meta">
            <span>ID: ${item.id}</span>
            <span>用户: ${item.userId}</span>
            <span>题材: ${item.genreId}</span>
            <span>章节字数: ${item.wordsPerChapter ?? "-"}</span>
        </div>
        <div class="list-item-content">${escapeHtml(item.idea || "")}</div>
    `);
}

async function refreshProviders() {
    const result = await api("/api/llm/providers");
    referenceData.providers = result.data || [];
    populateReferenceSelects();
    renderList(document.getElementById("providers-list"), referenceData.providers, (item) => `
        <div class="list-item-title">${escapeHtml(item.providerName)} <span class="badge">${escapeHtml(item.providerCode)}</span></div>
        <div class="list-item-meta">
            <span>ID: ${item.id}</span>
            <span>${item.enabled === 1 ? "enabled" : "disabled"}</span>
        </div>
        <div class="list-item-content">${escapeHtml(item.baseUrl || "")}</div>
    `);
}

async function refreshModels() {
    const result = await api("/api/llm/models");
    referenceData.models = result.data || [];
    populateReferenceSelects();
    renderList(document.getElementById("models-list"), referenceData.models, (item) => `
        <div class="list-item-title">${escapeHtml(item.modelName)} <span class="badge">${escapeHtml(item.modelCode)}</span></div>
        <div class="list-item-meta">
            <span>ID: ${item.id}</span>
            <span>Provider: ${item.providerId}</span>
            <span>Type: ${escapeHtml(item.modelType)}</span>
            <span>${item.enabled === 1 ? "enabled" : "disabled"}</span>
        </div>
        <div class="list-item-content">ctx=${item.contextWindow ?? "-"}, output=${item.maxOutputTokens ?? "-"}, stream=${item.supportsStream}, json=${item.supportsJson}, tools=${item.supportsTools}</div>
    `);
}

async function refreshScenes() {
    const result = await api("/api/llm/scenes");
    referenceData.scenes = result.data || [];
    populateReferenceSelects();
    renderList(document.getElementById("scenes-list"), referenceData.scenes, (item) => `
        <div class="list-item-title">${escapeHtml(item.sceneName)} <span class="badge">${escapeHtml(item.sceneCode)}</span></div>
        <div class="list-item-meta">
            <span>ID: ${item.id}</span>
            <span>Type: ${escapeHtml(item.sceneType)}</span>
            <span>${item.enabled === 1 ? "enabled" : "disabled"}</span>
        </div>
        <div class="list-item-content">${escapeHtml(item.description || "")}</div>
    `);
}

function renderOutlines(items) {
    renderList(document.getElementById("outlines-list"), items, (item) => `
        <div class="list-item-title">${escapeHtml(item.title || "(无标题)")} <span class="badge">${escapeHtml(item.outlineType)}</span></div>
        <div class="list-item-meta">
            <span>ID: ${item.id}</span>
            <span>小说: ${item.novelId}</span>
            <span>版本: ${item.version}</span>
            <span>${item.confirmed === 1 ? "confirmed" : "draft"}</span>
        </div>
        <div class="list-item-content">${escapeHtml(item.content || "")}</div>
    `);
}

function renderBindings(items) {
    renderList(document.getElementById("bindings-list"), items, (item) => `
        <div class="list-item-title">Binding #${item.id} <span class="badge">${escapeHtml(item.roleType)}</span></div>
        <div class="list-item-meta">
            <span>sceneId: ${item.sceneId}</span>
            <span>modelId: ${item.modelId}</span>
            <span>priority: ${item.priority}</span>
            <span>${item.enabled === 1 ? "enabled" : "disabled"}</span>
        </div>
        <div class="list-item-content">temperature=${valueOrDash(item.temperature)}, maxTokens=${valueOrDash(item.maxTokens)}, topP=${valueOrDash(item.topP)}, timeout=${valueOrDash(item.timeoutMs)}</div>
    `);
}

function renderOverrides(items) {
    renderList(document.getElementById("overrides-list"), items, (item) => `
        <div class="list-item-title">Override #${item.id} <span class="badge">${item.enabled === 1 ? "enabled" : "disabled"}</span></div>
        <div class="list-item-meta">
            <span>novelId: ${item.novelId}</span>
            <span>sceneId: ${item.sceneId}</span>
            <span>modelId: ${item.modelId}</span>
        </div>
        <div class="list-item-content">temperature=${valueOrDash(item.temperature)}, maxTokens=${valueOrDash(item.maxTokens)}, topP=${valueOrDash(item.topP)}, timeout=${valueOrDash(item.timeoutMs)}</div>
    `);
}

async function generateIdeas() {
    const button = document.getElementById("generate-ideas-btn");
    if (button?.dataset.loading === "1") {
        return;
    }
    const form = document.getElementById("novel-form");
    const genreId = form.elements.genreId.value;
    const genre = referenceData.genres.find((item) => String(item.id) === String(genreId));
    if (!genre) {
        toast("请先选择题材", true);
        return;
    }

    setLoadingState(button, true, "生成中...");
    document.getElementById("idea-results").innerHTML = `<div class="empty-box">正在生成创意，请稍候...</div>`;

    const body = {
        genreName: genre.name,
        userIdea: form.elements.idea.value,
        count: Number(document.getElementById("idea-count").value || 3),
        sceneCode: "idea_generate"
    };

    try {
        const result = await api("/api/ai/ideas/generate", { method: "POST", body });
        renderIdeaResults(result.data);
        toast("创意候选已生成");
    } catch (error) {
        document.getElementById("idea-results").innerHTML = `<div class="empty-box">创意生成失败，请重试。</div>`;
        throw error;
    } finally {
        setLoadingState(button, false, "生成创意");
    }
}

function renderIdeaResults(data) {
    const container = document.getElementById("idea-results");
    const ideas = data?.ideas || [];
    if (!ideas.length) {
        container.innerHTML = `<div class="empty-box">未返回可用创意。</div>`;
        return;
    }
    container.innerHTML = ideas.map((idea, index) => {
        const payload = encodeURIComponent(JSON.stringify(idea));
        return `
            <article class="assistant-card">
                <div class="assistant-card-title">
                    <span>创意 ${index + 1}：${escapeHtml(idea.title || "未命名")}</span>
                    <button type="button" class="ghost" data-action="apply-idea" data-payload="${payload}">采用</button>
                </div>
                <div class="assistant-card-body">${escapeHtml(idea.summary || "")}</div>
                <div class="assistant-card-meta">卖点：${escapeHtml(idea.sellingPoint || "")}</div>
            </article>
        `;
    }).join("");

    container.querySelectorAll('[data-action="apply-idea"]').forEach((button) => {
        button.addEventListener("click", () => {
            const idea = JSON.parse(decodeURIComponent(button.dataset.payload));
            const form = document.getElementById("novel-form");
            form.elements.idea.value = [idea.title, idea.summary, `卖点：${idea.sellingPoint}`]
                .filter(Boolean)
                .join("\n");
            if (!form.elements.title.value && idea.title) {
                form.elements.title.value = idea.title;
            }
            toast("已带入核心创意");
        });
    });
}

async function generateOutline() {
    const button = document.getElementById("generate-outline-btn");
    if (button?.dataset.loading === "1") {
        return;
    }
    const form = document.getElementById("outline-form");
    const novelId = form.elements.novelId.value;
    const outlineType = document.getElementById("outline-mode").value;
    if (!novelId) {
        toast("请先选择小说", true);
        return;
    }

    form.elements.outlineType.value = outlineType;

    setLoadingState(button, true, "生成中...");
    document.getElementById("outline-result").innerHTML = `<div class="empty-box">正在生成大纲，请稍候...</div>`;

    const body = {
        novelId,
        outlineType,
        volumeCount: outlineType === "volume" ? optionalNumber("outline-volume-count") : undefined,
        chaptersPerVolume: outlineType === "volume" ? optionalNumber("outline-chapters-per-volume") : undefined,
        volumeNo: outlineType === "chapter" ? optionalNumber("outline-volume-no") : undefined,
        startChapterNo: outlineType === "chapter" ? optionalNumber("outline-start-chapter-no") : undefined,
        endChapterNo: outlineType === "chapter" ? optionalNumber("outline-end-chapter-no") : undefined,
        userInstruction: document.getElementById("outline-user-instruction").value,
        sceneCode: "outline_generate"
    };

    try {
        const result = await api("/api/ai/outlines/generate", { method: "POST", body });
        renderOutlineResult(result.data);
        toast("大纲草案已生成");
    } catch (error) {
        document.getElementById("outline-result").innerHTML = `<div class="empty-box">大纲生成失败，请重试。</div>`;
        throw error;
    } finally {
        setLoadingState(button, false, "AI 生成大纲");
    }
}

function optionalNumber(id) {
    const value = document.getElementById(id)?.value;
    return value ? Number(value) : undefined;
}

function renderOutlineResult(data) {
    const container = document.getElementById("outline-result");
    if (!data || !data.content) {
        container.innerHTML = `<div class="empty-box">未返回可用大纲。</div>`;
        return;
    }

    const payload = encodeURIComponent(JSON.stringify(data));
    container.innerHTML = `
        <article class="assistant-card">
            <div class="assistant-card-title">
                <span>${escapeHtml(data.title || "大纲草案")}</span>
                <button type="button" class="ghost" id="apply-outline-btn" data-payload="${payload}">采用到表单</button>
            </div>
            <div class="assistant-card-body">${escapeHtml(data.content)}</div>
            <div class="assistant-card-meta">模型：${escapeHtml(data.route?.modelName || "-")} / 场景：${escapeHtml(data.route?.sceneCode || "-")}</div>
        </article>
    `;

    document.getElementById("apply-outline-btn")?.addEventListener("click", () => {
        const outline = JSON.parse(decodeURIComponent(document.getElementById("apply-outline-btn").dataset.payload));
        const form = document.getElementById("outline-form");
        form.elements.title.value = outline.title || "";
        form.elements.content.value = outline.content || "";
        toast("已带入大纲表单");
    });
}

function populateReferenceSelects() {
    populateSelectGroup("genres", referenceData.genres, (item) => ({
        value: item.id,
        label: item.name
    }));

    populateSelectGroup("novels", referenceData.novels, (item) => ({
        value: item.id,
        label: item.title
    }));

    populateSelectGroup("novels-optional", referenceData.novels, (item) => ({
        value: item.id,
        label: item.title
    }), { placeholder: "不指定小说" });

    populateSelectGroup("providers", referenceData.providers, (item) => ({
        value: item.id,
        label: item.providerName
    }));

    populateSelectGroup("models", referenceData.models, (item) => ({
        value: item.id,
        label: item.modelName
    }));

    populateSelectGroup("scenes", referenceData.scenes, (item) => ({
        value: item.id,
        label: item.sceneName
    }));

    populateSelectGroup("scene-codes", referenceData.scenes, (item) => ({
        value: item.sceneCode,
        label: item.sceneName
    }), { placeholder: "选择场景" });
}

function populateSelectGroup(group, items, mapFn, options = {}) {
    document.querySelectorAll(`[data-select="${group}"]`).forEach((select) => {
        const currentValue = select.value;
        const placeholder = options.placeholder ?? "请选择";
        const choices = items.map(mapFn);
        select.innerHTML = "";

        const first = document.createElement("option");
        first.value = "";
        first.textContent = placeholder;
        if (!select.required || options.placeholder) {
            select.appendChild(first);
        }

        choices.forEach((choice) => {
            const option = document.createElement("option");
            option.value = choice.value;
            option.textContent = choice.label;
            select.appendChild(option);
        });

        if (choices.some((choice) => String(choice.value) === String(currentValue))) {
            select.value = currentValue;
        } else if (select.required && choices.length > 0 && !options.placeholder) {
            select.value = choices[0].value;
        } else {
            select.value = "";
        }
    });
}

function renderList(container, items, template) {
    if (!items.length) {
        container.innerHTML = `<div class="empty">暂无数据</div>`;
        return;
    }
    container.innerHTML = items.map((item) => `<article class="list-item">${template(item)}</article>`).join("");
}

function bindSubmit(id, handler) {
    const form = document.getElementById(id);
    form?.addEventListener("submit", async (event) => {
        event.preventDefault();
        try {
            await handler(form);
        } catch (error) {
            toast(error.message || "请求失败", true);
        }
    });
}

async function api(url, options = {}) {
    const request = {
        method: options.method || "GET",
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        }
    };
    if (options.body !== undefined) {
        request.body = JSON.stringify(options.body);
    }

    const response = await fetch(url, request);
    const data = await response.json().catch(() => ({ code: response.status, message: "响应解析失败" }));
    if (!response.ok || data.code !== 0) {
        throw new Error(data.message || `请求失败: ${response.status}`);
    }
    return data;
}

function formJson(form, ...converters) {
    const data = Object.fromEntries(new FormData(form).entries());
    converters.forEach((converter) => converter(data));
    Object.keys(data).forEach((key) => {
        if (data[key] === "") {
            delete data[key];
        }
    });
    return data;
}

function toNumberFields(...fields) {
    return (data) => {
        fields.forEach((field) => {
            if (data[field] !== undefined && data[field] !== "") {
                data[field] = Number(data[field]);
            }
        });
    };
}

function toDecimalFields(...fields) {
    return (data) => {
        fields.forEach((field) => {
            if (data[field] !== undefined && data[field] !== "") {
                data[field] = Number(data[field]);
            }
        });
    };
}

function toast(message, isError = false) {
    const el = document.getElementById("toast");
    el.textContent = message;
    el.classList.remove("hidden", "error");
    if (isError) {
        el.classList.add("error");
    }
    clearTimeout(toast.timer);
    toast.timer = setTimeout(() => {
        el.classList.add("hidden");
    }, 2800);
}

function setLoadingState(button, loading, text) {
    if (!button) {
        return;
    }
    button.dataset.loading = loading ? "1" : "0";
    button.disabled = loading;
    button.textContent = text;
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

function valueOrDash(value) {
    return value === undefined || value === null || value === "" ? "-" : value;
}
