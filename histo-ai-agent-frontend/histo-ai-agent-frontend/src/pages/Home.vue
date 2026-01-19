<template>
  <section class="home">
    <div class="home__hero">
      <div class="home__badge">HISTO AI AGENT</div>
      <h1>选择你的 AI 应用</h1>
      <p>实时对话 · 流式响应 · 低延迟</p>
      <div class="home__meta">
        <span>Powered by SSE</span>
        <span>v1.0</span>
      </div>
      <div class="home__smile" aria-hidden="true">
        <span class="home__smile-eye"></span>
        <span class="home__smile-eye"></span>
        <span class="home__smile-mouth"></span>
      </div>
    </div>
    <div class="home__cards">
      <RouterLink to="/therapy" class="card">
        <div class="card__top">
          <div class="card__title">
            <img
              class="card__avatar"
              src="https://api.dicebear.com/7.x/bottts/svg?seed=therapy"
              alt="AI 心理治疗助手"
            />
            <h2>AI 心理治疗助手</h2>
          </div>
          <span class="card__chip">Therapy</span>
        </div>
        <p>温暖陪伴，实时聊天。</p>
        <div class="card__footer">
          <span>进入对话</span>
          <span class="card__arrow">→</span>
        </div>
      </RouterLink>
      <RouterLink to="/manus" class="card">
        <div class="card__top">
          <div class="card__title">
            <img
              class="card__avatar"
              src="https://api.dicebear.com/7.x/bottts/svg?seed=manus"
              alt="AI 超级智能体"
            />
            <h2>AI 超级智能体</h2>
          </div>
          <span class="card__chip">Manus</span>
        </div>
        <p>更强大的智能体，实时对话。</p>
        <div class="card__footer">
          <span>进入对话</span>
          <span class="card__arrow">→</span>
        </div>
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import { RouterLink } from "vue-router";
</script>

<style scoped>
.home {
  max-width: 1100px;
  margin: 0 auto;
  position: relative;
  padding: 28px;
  border-radius: 24px;
  background: radial-gradient(circle at top left, rgba(37, 99, 235, 0.18), transparent 45%),
    radial-gradient(circle at 15% 80%, rgba(14, 165, 233, 0.18), transparent 45%),
    #0b1220;
  color: #e5e7eb;
  border: 1px solid rgba(148, 163, 184, 0.2);
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.45);
}

.home::before {
  content: "";
  position: absolute;
  inset: 0;
  background-image: linear-gradient(rgba(148, 163, 184, 0.12) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 163, 184, 0.12) 1px, transparent 1px);
  background-size: 32px 32px;
  opacity: 0.3;
  pointer-events: none;
}

.home__hero {
  position: relative;
  margin-bottom: 36px;
}

.home__hero h1 {
  margin: 12px 0 8px;
  font-size: 34px;
  letter-spacing: 1px;
}

.home__hero p {
  margin: 0;
  color: #9ca3af;
  font-size: 15px;
}

.home__badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px;
  text-transform: uppercase;
  color: #e0f2fe;
  border: 1px solid rgba(56, 189, 248, 0.5);
  background: rgba(14, 165, 233, 0.18);
  box-shadow: 0 0 16px rgba(56, 189, 248, 0.35);
}

.home__meta {
  margin-top: 14px;
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #cbd5f5;
}

.home__meta span {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(148, 163, 184, 0.25);
}

.home__smile {
  position: absolute;
  right: 24px;
  top: 8px;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  border: 1px solid rgba(56, 189, 248, 0.5);
  background: radial-gradient(circle at 30% 30%, rgba(56, 189, 248, 0.25), transparent 55%),
    rgba(15, 23, 42, 0.9);
  box-shadow: 0 0 18px rgba(56, 189, 248, 0.45);
  display: grid;
  place-items: center;
  gap: 6px;
  animation: floatSmile 4s ease-in-out infinite;
}

.home__smile-eye {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #e0f2fe;
  box-shadow: 0 0 6px rgba(224, 242, 254, 0.7);
  animation: blinkSmile 4s ease-in-out infinite;
}

.home__smile-eye:first-child {
  position: absolute;
  left: 18px;
  top: 22px;
}

.home__smile-eye:nth-child(2) {
  position: absolute;
  right: 18px;
  top: 22px;
}

.home__smile-mouth {
  position: absolute;
  bottom: 18px;
  left: 50%;
  width: 26px;
  height: 14px;
  border-bottom: 3px solid #e0f2fe;
  border-radius: 0 0 30px 30px;
  transform: translateX(-50%);
  box-shadow: 0 0 8px rgba(224, 242, 254, 0.6);
}

.home__cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 22px;
}

.card {
  display: block;
  padding: 22px;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.85), rgba(15, 23, 42, 0.6));
  border-radius: 20px;
  border: 1px solid rgba(148, 163, 184, 0.25);
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
  color: #e5e7eb;
  backdrop-filter: blur(8px);
}

.card:hover {
  transform: translateY(-4px);
  border-color: rgba(56, 189, 248, 0.6);
  box-shadow: 0 16px 30px rgba(30, 64, 175, 0.35);
}

.card__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.card__title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card__avatar {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: rgba(15, 23, 42, 0.8);
  padding: 4px;
}

.card h2 {
  margin: 0 0 8px;
  font-size: 19px;
  letter-spacing: 0.4px;
}

.card p {
  margin: 0 0 18px;
  color: #9ca3af;
  line-height: 1.5;
}

.card__chip {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  color: #93c5fd;
  border: 1px solid rgba(59, 130, 246, 0.4);
  background: rgba(37, 99, 235, 0.12);
}

.card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #bfdbfe;
  font-size: 13px;
}

.card__arrow {
  font-size: 18px;
}

@media (max-width: 768px) {
  .home {
    padding: 20px;
  }

  .home__hero h1 {
    font-size: 24px;
  }

  .home__meta {
    flex-wrap: wrap;
  }

  .home__smile {
    position: static;
    margin-top: 16px;
  }
}

@media (max-width: 480px) {
  .home {
    padding: 16px;
  }

  .card {
    padding: 18px;
  }

  .card__top {
    flex-direction: column;
    align-items: flex-start;
  }
}

@keyframes floatSmile {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

@keyframes blinkSmile {
  0%,
  92%,
  100% {
    transform: scaleY(1);
  }
  95% {
    transform: scaleY(0.1);
  }
}
</style>
