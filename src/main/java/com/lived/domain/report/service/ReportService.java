package com.lived.domain.report.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.post.entity.Post;
import com.lived.domain.post.repository.CommentRepository;
import com.lived.domain.post.repository.PostRepository;
import com.lived.domain.report.converter.ReportConverter;
import com.lived.domain.report.dto.ReportRequestDTO;
import com.lived.domain.report.dto.ReportResponseDTO;
import com.lived.domain.report.entity.Report;
import com.lived.domain.report.entity.enums.ReportTargetType;
import com.lived.domain.report.repository.ReportRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

  private final ReportRepository reportRepository;
  private final MemberRepository memberRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  /**
   * 신고 생성
   */
  @Transactional
  public ReportResponseDTO.CreateReportResponse createReport(
      Long memberId,
      ReportRequestDTO.CreateReportRequest request
  ) {
    // Member 조회
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

    // 중복 신고 확인
    boolean alreadyReported = reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
        memberId, request.getTargetType(), request.getTargetId()
    );
    if (alreadyReported) {
      throw new GeneralException(GeneralErrorCode.REPORT_ALREADY_EXISTS);
    }

    // 신고 대상 존재 여부 확인
    if (request.getTargetType() == ReportTargetType.POST) {
      Post post = postRepository.findById(request.getTargetId())
          .orElseThrow(() -> new GeneralException(GeneralErrorCode.REPORT_TARGET_NOT_FOUND));

      // 삭제된 게시글인지 확인
      if (post.getDeletedAt() != null) {
        throw new GeneralException(GeneralErrorCode.REPORT_TARGET_NOT_FOUND);
      }
    } else if (request.getTargetType() == ReportTargetType.COMMENT) {
      com.lived.domain.comment.entity.Comment comment = commentRepository.findById(request.getTargetId())
          .orElseThrow(() -> new GeneralException(GeneralErrorCode.REPORT_TARGET_NOT_FOUND));

      // 삭제된 댓글인지 확인
      if (comment.getDeletedAt() != null) {
        throw new GeneralException(GeneralErrorCode.REPORT_TARGET_NOT_FOUND);
      }
    }

    Report report = ReportConverter.toReport(request, member);
    Report savedReport = reportRepository.save(report);

    return ReportConverter.toCreateReportResponse(savedReport);
  }
}