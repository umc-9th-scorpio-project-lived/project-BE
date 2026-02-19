package com.lived.domain.member.repository;

import com.lived.domain.member.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    // 생성일 기준 내림차순 정렬 (최신 공지가 위로 오도록)
    List<Announcement> findAllByOrderByCreatedAtDesc();
}
