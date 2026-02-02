package com.lived.domain.member.converter;

import com.lived.domain.member.dto.AnnouncementResponseDTO;
import com.lived.domain.member.entity.Announcement;

import java.util.List;
import java.util.stream.Collectors;

public class AnnouncementConverter {
    public static AnnouncementResponseDTO.AnnouncementInfoDTO toAnnouncementInfoDTO(Announcement announcement) {
        return AnnouncementResponseDTO.AnnouncementInfoDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .createdAt(announcement.getCreatedAt()) // BaseEntity의 getter
                .build();
    }

    public static AnnouncementResponseDTO.AnnouncementListDTO toAnnouncementListDTO(List<Announcement> announcementList) {
        List<AnnouncementResponseDTO.AnnouncementInfoDTO> announcementInfoDTOList = announcementList.stream()
                .map(AnnouncementConverter::toAnnouncementInfoDTO)
                .collect(Collectors.toList());

        return AnnouncementResponseDTO.AnnouncementListDTO.builder()
                .announcementList(announcementInfoDTOList)
                .build();
    }
}
