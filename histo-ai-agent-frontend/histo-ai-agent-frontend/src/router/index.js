import { createRouter, createWebHistory } from "vue-router";
import Home from "../pages/Home.vue";
import TherapyChat from "../pages/TherapyChat.vue";
import ManusChat from "../pages/ManusChat.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", name: "home", component: Home },
    { path: "/therapy", name: "therapy", component: TherapyChat },
    { path: "/manus", name: "manus", component: ManusChat }
  ]
});

export default router;
