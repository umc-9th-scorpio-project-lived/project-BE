package com.lived.domain.post.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.post.dto.SearchResponseDTO;
import com.lived.domain.post.entity.SearchHistory;
import com.lived.domain.post.repository.SearchHistoryRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

  private final SearchHistoryRepository searchHistoryRepository;
  private final MemberRepository memberRepository;

  /**
   * 검색어 저장
   */
  @Transactional
  protected void saveSearchHistory(Long memberId, String keyword) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

    // 기존에 같은 키워드가 있으면 삭제
    searchHistoryRepository.findByMemberIdAndKeyword(memberId, keyword)
        .ifPresent(searchHistoryRepository::delete);

    // 검색어 개수 확인
    long count = searchHistoryRepository.countByMemberId(memberId);

    // 10개 이상이면 가장 오래된 것 삭제
    if (count >= 10) {
      List<SearchHistory> histories = searchHistoryRepository
          .findByMemberIdOrderBySearchedAtDesc(memberId);
      if (!histories.isEmpty()) {
        searchHistoryRepository.delete(histories.get(histories.size() - 1));
      }
    }

    // 새 검색어 저장
    SearchHistory searchHistory = SearchHistory.builder()
        .member(member)
        .keyword(keyword)
        .build();
    searchHistoryRepository.save(searchHistory);
  }

  /**
   * 검색어 기록 조회
   */
  public SearchResponseDTO.SearchHistoryListResponse getSearchHistory(Long memberId) {
    List<SearchResponseDTO.SearchHistoryItem> histories =
        searchHistoryRepository.findByMemberIdOrderBySearchedAtDesc(memberId).stream()
            .limit(10)
            .map(h -> SearchResponseDTO.SearchHistoryItem.builder()
                .historyId(h.getId())
                .keyword(h.getKeyword())
                .searchedAt(h.getSearchedAt())
                .build())
            .toList();

    return SearchResponseDTO.SearchHistoryListResponse.builder()
        .histories(histories)
        .build();
  }

  /**
   * 검색어 개별 삭제
   */
  @Transactional
  public SearchResponseDTO.DeleteSearchHistoryResponse deleteSearchHistory(
      Long historyId,
      Long memberId
  ) {
    SearchHistory searchHistory = searchHistoryRepository.findById(historyId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND));

    // 본인의 검색어인지 확인
    if (!searchHistory.getMember().getId().equals(memberId)) {
      throw new GeneralException(GeneralErrorCode.FORBIDDEN);
    }

    searchHistoryRepository.delete(searchHistory);

    return SearchResponseDTO.DeleteSearchHistoryResponse.builder()
        .historyId(historyId)
        .build();
  }

  /**
   * 검색어 전체 삭제
   */
  @Transactional
  public SearchResponseDTO.DeleteAllSearchHistoryResponse deleteAllSearchHistory(Long memberId) {
    List<SearchHistory> histories = searchHistoryRepository.findByMemberIdOrderBySearchedAtDesc(memberId);

    int deletedCount = histories.size();
    searchHistoryRepository.deleteAll(histories);

    return SearchResponseDTO.DeleteAllSearchHistoryResponse.builder()
        .deletedCount(deletedCount)
        .build();
  }
}