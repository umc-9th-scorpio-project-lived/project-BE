package com.lived.domain.routine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lived.domain.concern.entity.mapping.ConcernRoutine;
import com.lived.domain.concern.repository.ConcernRoutineRepository;
import com.lived.domain.routine.converter.RoutineConverter;
import com.lived.domain.routine.dto.RoutineAiRecommendResponseDTO;
import com.lived.domain.routine.dto.RoutineRecommendResponseDTO;
import com.lived.domain.routine.dto.RoutineResponseDTO;
import com.lived.domain.routine.entity.Routine;
import com.lived.domain.routine.entity.RoutineCategory;
import com.lived.domain.routine.enums.CategoryName;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.domain.routine.repository.MemberRoutineRepository;
import com.lived.domain.routine.repository.RoutineRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineRecommendationService {

    private final ConcernRoutineRepository concernRoutineRepository;
    private final RoutineRepository routineRepository;
    private final MemberRoutineRepository memberRoutineRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.api-url}")
    private String baseUrl;

    // [온보딩] 고민 키워드 기반 루틴 추천
    public List<RoutineResponseDTO> getRecommendedRoutines(List<Long> concernIds) {
        List<ConcernRoutine> mappings = concernRoutineRepository.findAllByConcernIdIn(concernIds);

        List<Routine> routines = mappings.stream()
                .map(ConcernRoutine::getRoutine)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(routines);

        return routines.stream()
                .map(RoutineConverter::toRoutineResponseDTO)
                .toList();
    }

    // DB에 있는 추천 루틴 조회
    public RoutineRecommendResponseDTO getRecommendedRoutines() {
        List<Routine> allTemplates = routineRepository.findAllWithCategory();

        Map<CategoryName, List<Routine>> groupedByCategory = allTemplates.stream()
                .collect(Collectors.groupingBy(r -> r.getCategory().getName()));

        List<RoutineRecommendResponseDTO.CategorySectionDTO> categorySections = groupedByCategory.entrySet().
                stream().sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    CategoryName categoryName = entry.getKey();
                    List<Routine> routinesInCategory = entry.getValue();

                    RoutineCategory categoryEntity = routinesInCategory.getFirst().getCategory();

                    List<RoutineRecommendResponseDTO.RecommendItemDTO> items = routinesInCategory.stream()
                            .map(r -> RoutineRecommendResponseDTO.RecommendItemDTO.builder()
                                    .routineId(r.getId())
                                    .title(r.getTitle())
                                    .emoji(categoryEntity.getEmoji())
                                    .build())
                            .toList();

                    return RoutineRecommendResponseDTO.CategorySectionDTO.builder()
                            .categoryName(getKoreanCategoryName(categoryName))
                            .categoryEmoji(categoryEntity.getEmoji())
                            .routines(items)
                            .build();
                })
                .toList();

        return RoutineRecommendResponseDTO.builder()
                .categories(categorySections)
                .build();

    }

    private String getKoreanCategoryName(CategoryName name) {
        return switch (name) {
            case LIFESTYLE -> "생활 습관";
            case CLEANING -> "청소";
            case HEALTH -> "건강";
            case EATING_HABIT -> "식습관";
            case MINDFULNESS -> "마음 챙기기";
        };
    }

    // 사용자의 현재 루틴 기반 Gemini AI 추천
    public List<RoutineAiRecommendResponseDTO> getAiRecommendations(Long memberId) {

        // 현재 사용자의 활성화된 루틴 조회
        List<MemberRoutine> currentRoutines = memberRoutineRepository.findAllByMemberIdAndIsActiveTrue(memberId);

        String routineContext = currentRoutines.stream()
                .map(MemberRoutine::getTitle)
                .collect(Collectors.joining(", "));

        if (routineContext.isEmpty()) {
            routineContext = "없음 (루틴이 비어있으니 일반적인 건강 루틴을 추천해줘)";
        }

        String availableEmojis = "👍, 😁, 😂, 🤩, 🤪, 😤, 😶, 😍, 😪, 😎, " +
                "☕, 🥗, 🍎, 🥤, 🍕, 🍜, 🍱, 🍔, 🥐, 🍰, " +
                "🛌, 🏃, 📚, 💪, 🧘, 🎮, 🎨, 🎵, ✍️, 🍳, " +
                "☀️, 🌙, ⭐, 🌈, 🌸, 🌳, 🍃, 🌊, 🔥, ❄️, " +
                "🎧, 💻, 📖, ✏️, ⚽, 💡, ⏰, 📅, 🔔, 🎁, " +
                "💖, ❤️, 💔, 🤎, 💙, 🖤, 🤍, 🩶, ❤️‍🔥, 💗";

        // 프롬프트
        String prompt = "사용자의 현재 루틴은 [" + routineContext + "] 입니다.\n" +
                "이 루틴들과 함께하면 좋은 새로운 루틴들을 추천해주세요.\n" +
                "규칙:\n" +
                "1. 루틴 1개당 3개씩 추천할 것.\n" +
                "2. 응답은 반드시 JSON 배열 형식으로만 대답할 것.\n" +
                "3. 각 추천마다 baseRoutineTitle 필드에 반드시 [" + routineContext + "] 중 하나를 적을 것.\n" +
                "4. emoji 필드에는 반드시 다음 리스트에 있는 이모지만 사용할 것: [" + availableEmojis + "]\n" +
                "형식: [{\"title\": \"...\", \"emoji\": \"...\", \"baseRoutineTitle\": \"...\"}]";

        return callGeminiApi(prompt);

    }

    private List<RoutineAiRecommendResponseDTO> callGeminiApi(String prompt) {
        String url = baseUrl.trim() + "?key=" + apiKey.trim();

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        try {
            // JSON 구조 읽기
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestBody, String.class);

            // Gemini 응답 경로 추출
            JsonNode root = objectMapper.readTree(response.getBody());
            String jsonContent = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();


            // JSON 배열 부분([ ])만 추출하는 정규식
            if (jsonContent.contains("[") && jsonContent.contains("]")) {
                jsonContent = jsonContent.substring(jsonContent.indexOf("["), jsonContent.lastIndexOf("]") + 1);

                jsonContent = jsonContent.replaceAll("```json", "").replaceAll("```", "").trim();
            }

            // DTO 리스트로 변환
            return objectMapper.readValue(jsonContent,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RoutineAiRecommendResponseDTO.class));

        } catch (JsonProcessingException e) {
            log.error("Gemini JSON Parsing Error: ", e);
            throw new GeneralException(GeneralErrorCode.AI_RESPONSE_PARSE_ERROR);

        } catch (Exception e) {
            log.error("Gemini API Call Failed: ", e);
            throw new GeneralException(GeneralErrorCode.AI_RECOMMENDATION_FAILED);
        }
    }
}
