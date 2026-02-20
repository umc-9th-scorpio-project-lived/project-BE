package com.lived.domain.ai.service;

import com.lived.domain.ai.dto.GeminiRequest;
import com.lived.domain.ai.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api-url}")
    private String apiUrl;

    @Value("${gemini.api-key}")
    private String apiKey;

    public String getAiAdvice(String prompt) {
        try {
            log.info("Gemini 요청 시작 (Header Auth): API URL = {}", apiUrl);

            RestClient restClient = RestClient.create();

            GeminiResponse response = restClient.post()
                    .uri(apiUrl)
                    .header("x-goog-api-key", apiKey.trim()) // 헤더 인증 유지
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(GeminiRequest.create(prompt))
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response != null && !response.getCandidates().isEmpty()) {
                return response.getCandidates().get(0).getContent().getParts().get(0).getText();
            }
        } catch (Exception e) {
            log.error("Gemini API 호출 에러: {}", e.getMessage());
            return "꾸준함이 가장 큰 재능입니다! 오늘도 화이팅하세요.";
        }
        return "데이터를 분석 중입니다.";
    }
}