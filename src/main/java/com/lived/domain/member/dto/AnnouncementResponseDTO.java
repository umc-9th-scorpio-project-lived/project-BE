package com.lived.domain.member.dto;

import com.lived.domain.member.entity.Announcement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class AnnouncementResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementListDTO {
        private List<Announcement> announcementList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementInfoDTO {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
    }
}
