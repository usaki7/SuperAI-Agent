<template>
  <ChatView
    title="AI 超级智能体"
    subtitle="聊天室模式 · SSE 实时对话"
    ai-avatar="https://api.dicebear.com/7.x/bottts/svg?seed=manus"
    ai-message-mode="separate"
    theme="cool"
    :build-url="buildUrl"
  />
</template>

<script setup>
import ChatView from "../components/ChatView.vue";
import { buildSseUrl } from "../api/client";

const buildUrl = (message) => buildSseUrl("/ai/manus/chat", { message });
</script>
