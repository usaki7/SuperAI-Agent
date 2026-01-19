import { nextTick, onBeforeUnmount, ref, watch } from "vue";

export const useSseChat = (buildUrl, options = {}) => {
  const messages = ref([]);
  const input = ref("");
  const streaming = ref(false);
  const error = ref("");
  const listRef = ref(null);
  let eventSource = null;
  let hasReceived = false;
  const { aiMessageMode = "append" } = options;

  const closeStream = () => {
    if (eventSource) {
      eventSource.close();
      eventSource = null;
    }
  };

  const scrollToBottom = () => {
    if (!listRef.value) return;
    listRef.value.scrollTop = listRef.value.scrollHeight;
  };

  const send = () => {
    if (!input.value.trim() || streaming.value) return;
    error.value = "";
    const userText = input.value.trim();
    input.value = "";
    messages.value.push({
      id: `${Date.now()}-user`,
      role: "user",
      content: userText
    });

    let aiMessage = null;
    if (aiMessageMode === "append") {
      aiMessage = {
        id: `${Date.now()}-ai`,
        role: "ai",
        content: ""
      };
      messages.value.push(aiMessage);
    }

    const url = buildUrl(userText);
    streaming.value = true;
    hasReceived = false;
    closeStream();
    eventSource = new EventSource(url);

    eventSource.onmessage = (event) => {
      hasReceived = true;
      if (event.data === "[DONE]") {
        streaming.value = false;
        closeStream();
        return;
      }
      if (aiMessageMode === "separate") {
        messages.value.push({
          id: `${Date.now()}-ai-${messages.value.length}`,
          role: "ai",
          content: event.data
        });
      } else if (aiMessage) {
        aiMessage.content += event.data;
      }
    };

    eventSource.onerror = () => {
      // Spring SSE 会在正常结束时触发 onerror，此时应视为完成而非报错
      if (eventSource?.readyState === EventSource.CLOSED || hasReceived) {
        streaming.value = false;
        closeStream();
        return;
      }
      streaming.value = false;
      error.value = "连接中断，请稍后重试。";
      closeStream();
    };
  };

  watch(
    messages,
    () => nextTick(scrollToBottom),
    { deep: true }
  );

  onBeforeUnmount(() => {
    closeStream();
  });

  return {
    messages,
    input,
    streaming,
    error,
    listRef,
    send
  };
};
