package com.lived.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

public class GeminiRequest {
    public static GeminiRequestDto create(String text) {
        return new GeminiRequestDto(Collections.singletonList(
                new Content(Collections.singletonList(new Part(text)))
        ));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GeminiRequestDto {
        private List<Content> contents;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Part {
        private String text;
    }
}