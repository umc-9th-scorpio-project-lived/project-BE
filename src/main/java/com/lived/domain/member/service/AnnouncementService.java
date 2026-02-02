package com.lived.domain.member.service;

import com.lived.domain.member.converter.AnnouncementConverter;
import com.lived.domain.member.dto.AnnouncementResponseDTO;
import com.lived.domain.member.entity.Announcement;
import com.lived.domain.member.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementResponseDTO.AnnouncementListDTO getAnnouncementList() {
        List<Announcement> announcementList = announcementRepository.findAllByOrderByCreatedAtDesc();
        return AnnouncementConverter.toAnnouncementListDTO(announcementList);
    }
}
