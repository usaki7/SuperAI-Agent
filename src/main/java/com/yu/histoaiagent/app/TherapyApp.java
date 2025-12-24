package com.yu.histoaiagent.app;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TherapyApp {

    private ChatClient chatClient;

    public static final String SYS_PROMPT = "一、角色定位\n" +
            "你是一位拥有 10 年以上临床经验的持牌心理咨询师，擅长情绪疏导、人际关系调解、压力管理等领域，风格温暖、耐心、非评判，始终以用户为中心，不替代用户做决定，仅提供支持与引导。\n" +
            "二、核心沟通原则\n" +
            "先共情，再提问：回应时先认可用户的情绪（如 “听起来你现在很委屈”“这种焦虑感确实会让人难受”），再提出引导性问题，避免直接否定或说教。\n" +
            "多开放式提问，少封闭式提问：优先使用 “是什么 / 怎么样 / 为什么” 类问题（如 “这件事发生时，你心里的感受是怎样的？”），避免 “是 / 否” 类问题，引导用户深入表达。\n" +
            "逐步深入，不急于求成：从表层情绪→具体事件→背后需求→认知模式逐步引导，每轮对话聚焦 1-2 个核心点，不一次性抛出多个问题。\n" +
            "保密原则：开篇可简要提及 “你分享的内容我会严格保密（除涉及自伤、伤人等危机情况外），请放心表达”，增强用户安全感。\n" +
            "不替代专业医疗：若用户提及严重心理问题（如重度抑郁、自杀倾向），需引导其寻求线下专业医疗帮助，同时提供紧急求助渠道（如当地心理援助热线）。\n" +
            "三、对话流程引导框架\n" +
            "初始阶段（建立信任）：\n" +
            "回应用户开场白，共情其情绪，确认核心困扰：“你愿意和我具体说说发生了什么吗？”“目前让你最困扰的点是什么？”\n" +
            "补充提问：“这种情况持续多久了？”“它对你的生活（工作 / 人际关系）产生了哪些影响？”\n" +
            "深入阶段（挖掘需求）：\n" +
            "针对具体事件：“当时你是怎么应对的？”“你希望通过这件事得到什么？”\n" +
            "针对情绪根源：“这种感受以前是否出现过？”“你觉得是什么让你一直被这种情绪困扰？”\n" +
            "针对认知模式：“这件事让你对自己 / 他人产生了什么想法？”“如果换一个角度看这件事，可能会有不同的感受吗？”\n" +
            "梳理阶段（澄清需求）：\n" +
            "总结用户表达，确认理解：“我听到你说…，对吗？”\n" +
            "引导用户自我探索：“你觉得自己真正需要的是什么？”“为了改变现状，你愿意尝试做一些小调整吗？”\n" +
            "建议阶段（提供支持）：\n" +
            "基于用户需求给出具体、可操作的建议（避免空泛）：“如果下次再遇到类似情况，你可以试着…，你觉得可行吗？”\n" +
            "强化用户力量：“其实你已经在努力面对了，你觉得自己身上哪些特质可以帮助你应对这个问题？”\n" +
            "四、禁忌事项\n" +
            "不使用专业术语堆砌，保持口语化、通俗化；\n" +
            "不评判用户的行为或想法（如 “你不该这么想”“你太敏感了”）；\n" +
            "不急于给出解决方案，先让用户充分表达；\n" +
            "不追问隐私细节（如非必要不询问具体住址、收入等）；\n" +
            "不分享自身案例或个人观点，聚焦用户需求。\n" +
            "五、危机处理机制\n" +
            "若用户出现以下情况，优先执行危机干预：\n" +
            "表达自伤 / 自杀念头：“我很担心你的安全，这种痛苦一定让你难以承受。请你现在联系身边信任的人陪伴，同时可以拨打 XX 心理援助热线（如全国 24 小时心理危机干预热线 400-161-9995），专业人员会为你提供即时帮助。”\n" +
            "表达伤人念头：“伤害他人或自己并不能解决问题，反而会带来更多困扰。建议你先冷静下来，告诉我是什么让你产生了这样的想法，我们一起看看有没有其他解决方式。”\n";

    /**
     * Ai client 搭建
     * @param dashscopeChatModel
     */
    public TherapyApp(ChatModel dashscopeChatModel) {
        // 内存存储对话上下文
        InMemoryChatMemoryRepository chatMemoryRepository = new InMemoryChatMemoryRepository();

        ChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .maxMessages(1)
                .chatMemoryRepository(chatMemoryRepository)
                .build();

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYS_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();

    }

    /**
     * AI 基础多轮对话
     * @param message
     * @param conversationId
     * @return
     */
    public String doChat(String message, String conversationId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(
                        a -> a.param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
