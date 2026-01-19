<template>
  <section class="chat" :class="themeClass">
    <header class="chat__header">
      <div>
        <h1>{{ title }}</h1>
        <p>{{ subtitle }}</p>
      </div>
      <div v-if="chatId" class="chat__meta">
        <span>会话 ID</span>
        <strong>{{ chatId }}</strong>
      </div>
    </header>

    <div ref="listRef" class="chat__list">
      <div v-if="messages.length === 0" class="chat__empty">
        发送第一条消息开始对话。
      </div>
      <div
        v-for="item in messages"
        :key="item.id"
        class="chat__bubble"
        :class="item.role === 'user' ? 'chat__bubble--user' : 'chat__bubble--ai'"
      >
        <div v-if="item.role === 'ai'" class="chat__avatar">
          <img :src="aiAvatar" alt="AI" />
        </div>
        <div class="chat__message">
          <div class="chat__role">{{ item.role === "user" ? "你" : "AI" }}</div>
          <div class="chat__content">{{ item.content }}</div>
        </div>
      </div>
    </div>

    <div class="chat__footer">
      <div v-if="error" class="chat__error">{{ error }}</div>
      <div class="chat__input">
        <textarea
          v-model="input"
          placeholder="输入消息，按 Enter 发送，Shift+Enter 换行"
          @keydown="handleKeydown"
        ></textarea>
        <button :disabled="!canSend" @click="send">
          {{ streaming ? "AI 回复中..." : "发送" }}
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from "vue";
import { useSseChat } from "../composables/useSseChat";

const props = defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, required: true },
  chatId: { type: String, default: "" },
  buildUrl: { type: Function, required: true },
  aiAvatar: { type: String, default: "" },
  aiMessageMode: {
    type: String,
    default: "append",
    validator: (value) => ["append", "separate"].includes(value)
  },
  theme: {
    type: String,
    default: "neutral",
    validator: (value) => ["neutral", "warm", "cool"].includes(value)
  }
});

const { messages, input, streaming, error, listRef, send } = useSseChat(
  (message) => props.buildUrl(message),
  { aiMessageMode: props.aiMessageMode }
);

const themeClass = computed(() => `chat--${props.theme}`);

const canSend = computed(() => input.value.trim().length > 0 && !streaming.value);

const handleKeydown = (event) => {
  if (event.key === "Enter" && !event.shiftKey) {
    event.preventDefault();
    if (canSend.value) {
      send();
    }
  }
};
</script>

<style scoped>
.chat {
  max-width: 1040px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chat__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  background: rgba(15, 23, 42, 0.9);
  padding: 20px 24px;
  border-radius: 18px;
  border: 1px solid rgba(148, 163, 184, 0.25);
  gap: 16px;
  flex-wrap: wrap;
}

.chat__header h1 {
  margin: 0 0 6px;
  font-size: 22px;
}

.chat__header p {
  margin: 0;
  color: #94a3b8;
}

.chat__meta {
  text-align: right;
  font-size: 12px;
  color: #94a3b8;
}

.chat__meta strong {
  display: block;
  font-size: 13px;
  color: #e2e8f0;
}

.chat__list {
  height: 420px;
  overflow-y: auto;
  background: rgba(15, 23, 42, 0.85);
  padding: 20px;
  border-radius: 18px;
  border: 1px solid rgba(148, 163, 184, 0.25);
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.chat__empty {
  margin: auto;
  color: #94a3b8;
}

.chat__bubble {
  max-width: 74%;
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(30, 41, 59, 0.9);
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.chat__bubble--user {
  align-self: flex-end;
  background: rgba(37, 99, 235, 0.2);
  border: 1px solid rgba(59, 130, 246, 0.4);
}

.chat__bubble--ai {
  align-self: flex-start;
  background: rgba(14, 165, 233, 0.12);
  border: 1px solid rgba(56, 189, 248, 0.35);
}

.chat__avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 1px solid rgba(148, 163, 184, 0.4);
  background: rgba(15, 23, 42, 0.9);
}

.chat__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.chat__message {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.chat__role {
  font-size: 12px;
  color: #94a3b8;
}

.chat__content {
  white-space: pre-wrap;
  line-height: 1.5;
  text-align: left;
  color: #e2e8f0;
  max-width: 100%;
  overflow-wrap: anywhere;
  word-break: break-word;
}

.chat__footer {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chat__error {
  color: #dc2626;
  font-size: 13px;
}

.chat__input {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  background: rgba(15, 23, 42, 0.9);
  padding: 16px;
  border-radius: 18px;
  border: 1px solid rgba(148, 163, 184, 0.25);
}

.chat__input textarea {
  width: 100%;
  min-height: 90px;
  border: 1px solid rgba(148, 163, 184, 0.3);
  border-radius: 12px;
  padding: 10px 12px;
  resize: none;
  text-align: left;
  background: rgba(15, 23, 42, 0.7);
  color: #e2e8f0;
}

.chat__input button {
  min-width: 120px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #2563eb, #0ea5e9);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  padding: 0 16px;
  box-shadow: 0 10px 20px rgba(37, 99, 235, 0.3);
}

.chat__input button:disabled {
  cursor: not-allowed;
  background: rgba(59, 130, 246, 0.4);
  box-shadow: none;
}

.chat--warm .chat__header {
  background: rgba(30, 41, 59, 0.75);
  border-color: rgba(251, 191, 36, 0.35);
}

.chat--warm .chat__list {
  background: rgba(30, 41, 59, 0.7);
  border-color: rgba(251, 191, 36, 0.3);
}

.chat--warm .chat__bubble--ai {
  background: rgba(251, 191, 36, 0.12);
  border-color: rgba(251, 191, 36, 0.45);
}

.chat--warm .chat__bubble--user {
  background: rgba(251, 113, 133, 0.14);
  border-color: rgba(244, 114, 182, 0.4);
}

.chat--warm .chat__input {
  border-color: rgba(251, 191, 36, 0.35);
}

.chat--warm .chat__input button {
  background: linear-gradient(135deg, #f97316, #f59e0b);
  box-shadow: 0 10px 20px rgba(249, 115, 22, 0.35);
}

.chat--cool .chat__header {
  background: rgba(2, 6, 23, 0.9);
  border-color: rgba(56, 189, 248, 0.3);
}

.chat--cool .chat__list {
  background: rgba(2, 6, 23, 0.8);
  border-color: rgba(56, 189, 248, 0.25);
}

.chat--cool .chat__bubble--ai {
  background: rgba(14, 165, 233, 0.08);
  border-color: rgba(56, 189, 248, 0.4);
}

.chat--cool .chat__bubble--user {
  background: rgba(37, 99, 235, 0.12);
  border-color: rgba(37, 99, 235, 0.5);
}

.chat--cool .chat__input {
  border-color: rgba(56, 189, 248, 0.3);
}

.chat--cool .chat__input button {
  background: linear-gradient(135deg, #0ea5e9, #2563eb);
  box-shadow: 0 10px 20px rgba(14, 165, 233, 0.35);
}

@media (max-width: 1024px) {
  .chat {
    max-width: 100%;
  }

  .chat__list {
    height: 380px;
  }
}

@media (max-width: 768px) {
  .chat__header {
    padding: 16px;
  }

  .chat__list {
    height: 340px;
    padding: 16px;
  }

  .chat__bubble {
    max-width: 100%;
  }

  .chat__input {
    grid-template-columns: 1fr;
  }

  .chat__input button {
    width: 100%;
    min-height: 44px;
  }
}

@media (max-width: 480px) {
  .chat__header h1 {
    font-size: 18px;
  }

  .chat__header p {
    font-size: 13px;
  }
}
</style>
