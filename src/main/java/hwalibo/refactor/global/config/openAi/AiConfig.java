package hwalibo.refactor.global.config.openAi;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                    당신은 화장실 리뷰 요약 전문가입니다. 
                    사용자가 제공하는 리뷰들을 분석하여 핵심 내용을 200바이트(한글 약 66자) 이내로 요약하세요.
                    반드시 한국어로 답변하고, 마침표로 끝나는 완성된 문장으로 작성하세요.
                    """)
                .build();
    }
}