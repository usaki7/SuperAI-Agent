<template>
  <ChatView
    title="AI 心理治疗助手"
    subtitle="聊天室模式 · SSE 实时对话"
    :chat-id="chatId"
    ai-avatar="https://api.dicebear.com/7.x/bottts/svg?seed=therapy"
    theme="warm"
    :build-url="buildUrl"
  />
</template>

<script setup>
import { onMounted, ref } from "vue";
import ChatView from "../components/ChatView.vue";
import { buildSseUrl } from "../api/client";

const chatId = ref("");

const createChatId = () => {
  if (crypto?.randomUUID) {
    return crypto.randomUUID();
  }
  return `chat-${Date.now()}-${Math.random().toString(16).slice(2)}`;
};

onMounted(() => {
  chatId.value = createChatId();
});

const buildUrl = (message) =>
  buildSseUrl("/ai/therapy_app/chat/sse", { message, chatId: chatId.value });
</script>
