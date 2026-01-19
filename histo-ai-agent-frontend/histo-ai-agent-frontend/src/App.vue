<template>
  <div class="app">
    <header class="app__header">
      <div class="app__brand">Histo AI Agent</div>
      <nav class="app__nav">
        <RouterLink to="/">主页</RouterLink>
        <RouterLink to="/therapy">心理治疗助手</RouterLink>
        <RouterLink to="/manus">超级智能体</RouterLink>
      </nav>
    </header>
    <main class="app__main">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from "vue-router";
</script>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 32px;
  background: rgba(11, 17, 32, 0.9);
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
  backdrop-filter: blur(10px);
  gap: 16px;
  flex-wrap: wrap;
}

.app__brand {
  font-weight: 700;
  font-size: 18px;
  letter-spacing: 1px;
  color: #e0f2fe;
  text-shadow: 0 0 12px rgba(56, 189, 248, 0.5);
}

.app__nav {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #cbd5f5;
  flex-wrap: wrap;
}

.app__nav a {
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid transparent;
  transition: border-color 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;
}

.app__nav a:hover {
  border-color: rgba(56, 189, 248, 0.5);
  color: #e0f2fe;
  box-shadow: 0 0 12px rgba(56, 189, 248, 0.25);
}

.app__nav a.router-link-active {
  color: #93c5fd;
  font-weight: 600;
  border-color: rgba(59, 130, 246, 0.6);
  box-shadow: 0 0 12px rgba(59, 130, 246, 0.35);
}

.app__main {
  flex: 1;
  padding: 24px;
}

@media (max-width: 768px) {
  .app__header {
    padding: 16px 20px;
  }

  .app__main {
    padding: 16px;
  }
}
</style>
