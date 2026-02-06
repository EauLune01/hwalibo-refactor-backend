package hwalibo.refactor.global.utils;

import hwalibo.refactor.review.dto.result.ReviewSummaryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAISummaryProvider {

    private final ChatClient chatClient;

    public String getSummaryFromOpenAI(String combinedText) {
        String summary = chatClient.prompt()
                .user(u -> u
                        .text("제공된 화장실 리뷰와 태그들을 바탕으로 핵심만 요약하세요: {review}")
                        .param("review", combinedText)
                )
                .call()
                .content();

        if (summary == null || summary.isBlank()) {
            throw new RuntimeException("AI 요약 결과 생성 실패");
        }

        return formatToMaxBytes(summary, 200);
    }

    /******************** Helper Method ********************/
    private static final Set<Character> END_PUNCT = Set.of('.', '!', '?', '。', '！', '？', '…');

    private String formatToMaxBytes(String text, int maxBytes) {
        String s = (text == null ? "" : text).trim().replaceAll("\\s+", " ");
        if (utf8Len(s) <= maxBytes) return ensureSentenceClosed(s, maxBytes);

        int endIdx = lastSentenceEndWithinBytes(s, maxBytes);
        if (endIdx >= 0) {
            return s.substring(0, endIdx + 1);
        }
        String truncated = truncateUtf8(s, Math.max(0, maxBytes - 3));
        return truncated + "…";
    }

    private String ensureSentenceClosed(String s, int maxBytes) {
        if (!s.isEmpty() && END_PUNCT.contains(s.charAt(s.length() - 1))) return s;
        if (utf8Len(s) + 1 <= maxBytes) return s + ".";
        return truncateUtf8(s, Math.max(0, maxBytes - 1)) + ".";
    }

    private int lastSentenceEndWithinBytes(String s, int maxBytes) {
        int bytes = 0;
        int lastEnd = -1;
        for (int i = 0; i < s.length(); i++) {
            int b = s.substring(i, i + 1).getBytes(StandardCharsets.UTF_8).length;
            if (bytes + b > maxBytes) break;
            char ch = s.charAt(i);
            bytes += b;
            if (END_PUNCT.contains(ch)) lastEnd = i;
        }
        return lastEnd;
    }

    private int utf8Len(String s) {
        return s.getBytes(StandardCharsets.UTF_8).length;
    }

    private String truncateUtf8(String text, int maxBytes) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= maxBytes) return text;
        int cut = maxBytes;
        while (cut > 0 && (bytes[cut] & 0xC0) == 0x80) cut--;
        return new String(bytes, 0, cut, StandardCharsets.UTF_8);
    }
}