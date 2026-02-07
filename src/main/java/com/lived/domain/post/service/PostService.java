package com.lived.domain.post.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberBlockRepository;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.post.converter.PostConverter;
import com.lived.domain.post.dto.PostRequestDTO;
import com.lived.domain.post.dto.PostResponseDTO;
import com.lived.domain.post.entity.Post;
import com.lived.domain.post.entity.PostImage;
import com.lived.domain.post.entity.enums.PostCategory;
import com.lived.domain.report.entity.enums.ReportTargetType;
import com.lived.domain.post.entity.mapping.PostLike;
import com.lived.domain.post.entity.mapping.PostScrap;
import com.lived.domain.post.repository.*;
import com.lived.domain.report.repository.ReportRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import com.lived.global.dto.CursorPageResponse;
import com.lived.global.s3.S3Service;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final PostImageRepository postImageRepository;
  private final MemberRepository memberRepository;
  private final S3Service s3Service;
  private final PostLikeRepository postLikeRepository;
  private final PostScrapRepository postScrapRepository;
  private final MemberBlockRepository memberBlockRepository;
  private final CommentRepository commentRepository;
  private final SearchService searchService;
  private final ReportRepository reportRepository;

  private static final int MAX_IMAGE_COUNT = 10;

  /**
   * 게시글 작성
   */
  @Transactional
  public PostResponseDTO.CreatePostResponse createPost(
      PostRequestDTO.CreatePostRequest request,
      List<MultipartFile> images,
      Long memberId
  ) {
    // 이미지 개수 검증
    if (images != null && images.size() > MAX_IMAGE_COUNT) {
      throw new GeneralException(GeneralErrorCode.POST_IMAGE_LIMIT_EXCEEDED);
    }

    // Member 조회
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

    // Post 생성
    Post post = PostConverter.toPost(request, member);
    Post savedPost = postRepository.save(post);

    // 이미지 업로드 및 PostImage 생성
    if (images != null && !images.isEmpty()) {
      List<PostImage> postImages = new ArrayList<>();

      for (int i = 0; i < images.size(); i++) {
        MultipartFile image = images.get(i);

        // S3 업로드
        String imageUrl = s3Service.uploadPostImage(image, savedPost.getId());

        // PostImage 엔티티 생성
        PostImage postImage = PostConverter.toPostImage(savedPost, imageUrl, i + 1);
        postImages.add(postImage);
      }

      postImageRepository.saveAll(postImages);
    }

    return PostConverter.toCreatePostResponse(savedPost);
  }

  /**
   * 게시글 수정
   */
  @Transactional
  public PostResponseDTO.UpdatePostResponse updatePost(
      Long postId,
      Long memberId,
      PostRequestDTO.UpdatePostRequest request,
      List<MultipartFile> images
  ) {
    // Post 조회
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.POST_NOT_FOUND));

    // 권한 확인 (작성자만 수정 가능)
    if (!post.getMember().getId().equals(memberId)) {
      throw new GeneralException(GeneralErrorCode.POST_FORBIDDEN);
    }

    // 기존 이미지 개수 확인
    List<PostImage> existingImages = postImageRepository.findAllByPostId(postId);
    int existingImageCount = existingImages.size();

    // 삭제할 이미지 처리
    if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
      List<PostImage> imagesToDelete = postImageRepository.findAllById(request.getDeleteImageIds());

      // S3에서 삭제
      for (PostImage image : imagesToDelete) {
        s3Service.deleteFile(image.getImageUrl());
      }

      // DB에서 삭제
      postImageRepository.deleteAll(imagesToDelete);
      existingImageCount -= imagesToDelete.size();
    }

    // 새 이미지 추가 검증
    int newImageCount = (images != null) ? images.size() : 0;
    if (existingImageCount + newImageCount > MAX_IMAGE_COUNT) {
      throw new GeneralException(GeneralErrorCode.POST_IMAGE_LIMIT_EXCEEDED);
    }

    // 기존 이미지 순서 재배치
    // 기존 이미지 순서 재배치
    if (request.getImageOrders() != null && !request.getImageOrders().isEmpty()) {
      List<PostImage> allImages = postImageRepository.findAllByPostId(postId);

      for (PostImage img : allImages) {
        img.updateOrderIndex(-img.getId().intValue());
      }
      postImageRepository.flush();

      for (PostRequestDTO.UpdatePostRequest.ImageOrder order : request.getImageOrders()) {
        allImages.stream()
            .filter(img -> img.getId().equals(order.getImageId()))
            .findFirst()
            .ifPresent(img -> img.updateOrderIndex(order.getOrderIndex()));
      }
    }

    // 새 이미지 업로드 및 저장
    if (images != null && !images.isEmpty()) {
      List<PostImage> newPostImages = new ArrayList<>();

      // 현재 최대 orderIndex 찾기
      int maxOrderIndex = postImageRepository.findAllByPostId(postId).stream()
          .mapToInt(PostImage::getOrderIndex)
          .max()
          .orElse(0);

      for (int i = 0; i < images.size(); i++) {
        MultipartFile image = images.get(i);

        // S3 업로드
        String imageUrl = s3Service.uploadPostImage(image, postId);

        // PostImage 엔티티 생성
        PostImage postImage = PostConverter.toPostImage(post, imageUrl, maxOrderIndex + i + 1);
        newPostImages.add(postImage);
      }

      postImageRepository.saveAll(newPostImages);
    }

    post.update(request);

    return PostConverter.toUpdatePostResponse(post);
  }

  /**
   * 게시글 삭제
   */
  @Transactional
  public PostResponseDTO.DeletePostResponse deletePost(Long postId, Long memberId) {
    // Post 조회
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.POST_NOT_FOUND));

    // 이미 삭제된 게시글인지 확인
    if (post.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.POST_NOT_FOUND);
    }

    // 권한 확인 (작성자만 삭제 가능)
    if (!post.getMember().getId().equals(memberId)) {
      throw new GeneralException(GeneralErrorCode.POST_FORBIDDEN);
    }

    post.delete();

    // S3에서 이미지 삭제
    List<PostImage> images = postImageRepository.findAllByPostId(postId);
    for (PostImage image : images) {
      s3Service.deleteFile(image.getImageUrl());
    }
    postImageRepository.deleteAll(images);

    return PostConverter.toDeletePostResponse(post);
  }

  /**
   * 게시글 좋아요 토글
   */
  @Transactional
  public PostResponseDTO.ToggleLikeResponse toggleLike(Long postId, Long memberId) {
    // Post 조회
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.POST_NOT_FOUND));

    // 삭제된 게시글인지 확인
    if (post.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.POST_NOT_FOUND);
    }

    // Member 조회
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

    // 좋아요 존재 여부 확인
    Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndMemberId(postId, memberId);

    boolean isLiked;

    if (existingLike.isPresent()) {
      // 좋아요 취소
      postLikeRepository.delete(existingLike.get());
      post.decrementLikeCount();
      isLiked = false;
    } else {
      // 좋아요 추가
      PostLike postLike = PostLike.builder()
          .post(post)
          .member(member)
          .build();
      postLikeRepository.save(postLike);
      post.incrementLikeCount();
      isLiked = true;
    }

    return PostConverter.toToggleLikeResponse(isLiked, post.getLikeCount());
  }

  /**
   * 게시글 스크랩 토글
   */
  @Transactional
  public PostResponseDTO.ToggleScrapResponse toggleScrap(Long postId, Long memberId) {
    // Post 조회
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.POST_NOT_FOUND));

    // 삭제된 게시글인지 확인
    if (post.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.POST_NOT_FOUND);
    }

    // Member 조회
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

    // 스크랩 존재 여부 확인
    Optional<PostScrap> existingScrap = postScrapRepository.findByPostIdAndMemberId(postId,
        memberId);

    boolean isScrapped;

    if (existingScrap.isPresent()) {
      // 스크랩 취소
      postScrapRepository.delete(existingScrap.get());
      post.decrementScrapCount();
      isScrapped = false;
    } else {
      // 스크랩 추가
      PostScrap postScrap = PostScrap.builder()
          .post(post)
          .member(member)
          .build();
      postScrapRepository.save(postScrap);
      post.incrementScrapCount();
      isScrapped = true;
    }

    return PostConverter.toToggleScrapResponse(isScrapped);
  }

  /**
   * 게시글 목록 조회
   */
  @Transactional
  public CursorPageResponse<PostResponseDTO.PostListItem> getPostList(
      Long memberId,
      String keyword,
      PostCategory category,
      Long cursor,
      int size
  ) {
    // 검색어 저장
    if (keyword != null && !keyword.isBlank()) {
      searchService.saveSearchHistory(memberId, keyword);
    }

    List<Post> posts = postRepository.findAll();

    // 필터링
    List<PostResponseDTO.PostListItem> filteredPosts = posts.stream()
        .filter(p -> p.getDeletedAt() == null)
        .filter(p -> category == null || p.getCategory() == category)
        .filter(p -> keyword == null || keyword.isBlank() ||
            p.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
            p.getContent().toLowerCase().contains(keyword.toLowerCase()))
        .filter(p -> cursor == null || p.getId() < cursor)
        .filter(p -> {
          // 신고/차단 확인
          boolean isReported = reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
              memberId, ReportTargetType.POST, p.getId()
          );
          boolean isBlocked = memberBlockRepository.existsByBlockerIdAndBlockedId(
              memberId, p.getMember().getId()
          );
          return !isReported && !isBlocked;
        })
        .sorted((a, b) -> b.getId().compareTo(a.getId()))
        .limit(size + 1)
        .map(p -> {

          String thumbnailUrl = postImageRepository.findFirstByPostIdAndOrderIndex(p.getId(), 1)
              .map(PostImage::getImageUrl)
              .orElse(null);

          int imageCount = postImageRepository.findAllByPostId(p.getId()).size();

          return PostResponseDTO.PostListItem.builder()
              .postId(p.getId())
              .category(p.getCategory())
              .categoryLabel(p.getCategory().getLabel())
              .title(p.getTitle())
              .content(p.getContent())
              .likeCount(p.getLikeCount())
              .commentCount(p.getCommentCount())
              .thumbnailUrl(thumbnailUrl)
              .imageCount(imageCount)
              .createdAt(p.getCreatedAt())
              .build();
        })
        .toList();

    boolean hasNext = filteredPosts.size() > size;
    List<PostResponseDTO.PostListItem> content = hasNext
        ? filteredPosts.subList(0, size)
        : filteredPosts;

    Long nextCursor = hasNext && !content.isEmpty()
        ? content.get(content.size() - 1).getPostId()
        : null;

    return CursorPageResponse.<PostResponseDTO.PostListItem>builder()
        .content(content)
        .hasNext(hasNext)
        .nextCursor(nextCursor)
        .build();
  }

  /**
   * 실시간 인기글 조회
   * 조건: 24시간 내 작성, 좋아요 15개 이상, 최대 5개 반환
   */
  public PostResponseDTO.PopularPostListResponse getPopularPosts() {
    LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

    List<PostResponseDTO.PopularPostItem> popularPosts = postRepository.findAll().stream()
        .filter(p -> p.getDeletedAt() == null)
        .filter(p -> p.getCreatedAt().isAfter(oneDayAgo))
        .filter(p -> p.getLikeCount() >= 15)
        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
        .limit(5)
        .map(p -> PostResponseDTO.PopularPostItem.builder()
            .postId(p.getId())
            .title(p.getTitle())
            .content(p.getContent())
            .likeCount(p.getLikeCount())
            .commentCount(p.getCommentCount())
            .createdAt(p.getCreatedAt())
            .build())
        .toList();

    return PostResponseDTO.PopularPostListResponse.builder()
        .content(popularPosts)
        .build();
  }

  /**
   * 게시글 상세 조회
   */
  @Transactional
  public PostResponseDTO.PostDetailResponse getPostDetail(Long postId, Long memberId) {
    // Post 조회
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.POST_NOT_FOUND));

    // 삭제된 게시글인지 확인
    if (post.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.POST_NOT_FOUND);
    }

    // 신고/차단 확인
    boolean isReported = reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
        memberId, ReportTargetType.POST, postId
    );
    boolean isBlocked = memberBlockRepository.existsByBlockerIdAndBlockedId(
        memberId, post.getMember().getId()
    );

    if (isReported || isBlocked) {
      throw new GeneralException(GeneralErrorCode.POST_NOT_FOUND);
    }

    // 조회수 증가
    post.incrementViewCount();

    // 좋아요 여부 확인
    boolean isLiked = postLikeRepository.existsByPostIdAndMemberId(postId, memberId);

    // 스크랩 여부 확인
    boolean isScrapped = postScrapRepository.existsByPostIdAndMemberId(postId, memberId);

    // 이미지 목록 조회
    List<PostResponseDTO.PostImageInfo> images = postImageRepository.findAllByPostId(postId).stream()
        .sorted((a, b) -> a.getOrderIndex().compareTo(b.getOrderIndex()))
        .map(img -> PostResponseDTO.PostImageInfo.builder()
            .imageId(img.getId())
            .imageUrl(img.getImageUrl())
            .orderIndex(img.getOrderIndex())
            .build())
        .toList();

    // 작성자 정보
    PostResponseDTO.AuthorInfo authorInfo = PostResponseDTO.AuthorInfo.builder()
        .userId(post.getMember().getId())
        .nickname(post.getMember().getNickname())
        .profileImageUrl(post.getMember().getProfileImageUrl())
        .build();

    return PostResponseDTO.PostDetailResponse.builder()
        .postId(post.getId())
        .category(post.getCategory())
        .categoryLabel(post.getCategory().getLabel())
        .title(post.getTitle())
        .content(post.getContent())
        .viewCount(post.getViewCount())
        .likeCount(post.getLikeCount())
        .commentCount(post.getCommentCount())
        .isLiked(isLiked)
        .isScrapped(isScrapped)
        .createdAt(post.getCreatedAt())
        .author(authorInfo)
        .images(images)
        .build();
  }

  /**
   * 내가 작성한 게시글 목록 조회
   */
  public CursorPageResponse<PostResponseDTO.PostListItem> getMyPosts(
      Long memberId,
      Long cursor,
      int size
  ) {
    List<Post> posts = postRepository.findAll();

    // 필터링
    List<PostResponseDTO.PostListItem> myPosts = posts.stream()
        .filter(p -> p.getDeletedAt() == null)
        .filter(p -> p.getMember().getId().equals(memberId))
        .filter(p -> cursor == null || p.getId() < cursor)
        .sorted((a, b) -> b.getId().compareTo(a.getId()))
        .limit(size + 1)
        .map(p -> {
          // 썸네일 조회 (orderIndex = 1)
          String thumbnailUrl = postImageRepository.findFirstByPostIdAndOrderIndex(p.getId(), 1)
              .map(PostImage::getImageUrl)
              .orElse(null);

          // 전체 이미지 개수
          int imageCount = postImageRepository.findAllByPostId(p.getId()).size();

          return PostResponseDTO.PostListItem.builder()
              .postId(p.getId())
              .category(p.getCategory())
              .categoryLabel(p.getCategory().getLabel())
              .title(p.getTitle())
              .content(p.getContent())
              .likeCount(p.getLikeCount())
              .commentCount(p.getCommentCount())
              .thumbnailUrl(thumbnailUrl)
              .imageCount(imageCount)
              .createdAt(p.getCreatedAt())
              .build();
        })
        .toList();

    boolean hasNext = myPosts.size() > size;
    List<PostResponseDTO.PostListItem> content = hasNext
        ? myPosts.subList(0, size)
        : myPosts;

    Long nextCursor = hasNext && !content.isEmpty()
        ? content.get(content.size() - 1).getPostId()
        : null;

    return CursorPageResponse.<PostResponseDTO.PostListItem>builder()
        .content(content)
        .hasNext(hasNext)
        .nextCursor(nextCursor)
        .build();
  }

  /**
   * 내가 댓글 단 게시글 목록 조회
   */
  public CursorPageResponse<PostResponseDTO.PostListItem> getMyCommentedPosts(
      Long memberId,
      Long cursor,
      int size
  ) {
    // 내가 댓글 단 게시글 ID 목록 (중복 제거)
    List<Long> commentedPostIds = commentRepository.findAll().stream()
        .filter(c -> c.getMember().getId().equals(memberId))
        .filter(c -> c.getDeletedAt() == null)
        .map(c -> c.getPost().getId())
        .distinct()
        .toList();

    List<Post> posts = postRepository.findAll();

    // 필터링
    List<PostResponseDTO.PostListItem> commentedPosts = posts.stream()
        .filter(p -> p.getDeletedAt() == null)
        .filter(p -> commentedPostIds.contains(p.getId()))
        .filter(p -> cursor == null || p.getId() < cursor)
        .filter(p -> {
          boolean isReported = reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
              memberId, ReportTargetType.POST, p.getId()
          );
          boolean isBlocked = memberBlockRepository.existsByBlockerIdAndBlockedId(
              memberId, p.getMember().getId()
          );
          return !isReported && !isBlocked;
        })
        .sorted((a, b) -> b.getId().compareTo(a.getId()))
        .limit(size + 1)
        .map(p -> {
          // 썸네일 조회 (orderIndex = 1)
          String thumbnailUrl = postImageRepository.findFirstByPostIdAndOrderIndex(p.getId(), 1)
              .map(PostImage::getImageUrl)
              .orElse(null);

          // 전체 이미지 개수
          int imageCount = postImageRepository.findAllByPostId(p.getId()).size();

          return PostResponseDTO.PostListItem.builder()
              .postId(p.getId())
              .category(p.getCategory())
              .categoryLabel(p.getCategory().getLabel())
              .title(p.getTitle())
              .content(p.getContent())
              .likeCount(p.getLikeCount())
              .commentCount(p.getCommentCount())
              .thumbnailUrl(thumbnailUrl)
              .imageCount(imageCount)
              .createdAt(p.getCreatedAt())
              .build();
        })
        .toList();

    boolean hasNext = commentedPosts.size() > size;
    List<PostResponseDTO.PostListItem> content = hasNext
        ? commentedPosts.subList(0, size)
        : commentedPosts;

    Long nextCursor = hasNext && !content.isEmpty()
        ? content.get(content.size() - 1).getPostId()
        : null;

    return CursorPageResponse.<PostResponseDTO.PostListItem>builder()
        .content(content)
        .hasNext(hasNext)
        .nextCursor(nextCursor)
        .build();
  }

  /**
   * 내가 스크랩한 게시글 목록 조회
   */
  public CursorPageResponse<PostResponseDTO.PostListItem> getMyScrappedPosts(
      Long memberId,
      Long cursor,
      int size
  ) {
    // 내가 스크랩한 게시글 ID 목록
    List<Long> scrappedPostIds = postScrapRepository.findAll().stream()
        .filter(s -> s.getMember().getId().equals(memberId))
        .map(s -> s.getPost().getId())
        .toList();

    List<Post> posts = postRepository.findAll();

    // 필터링
    List<PostResponseDTO.PostListItem> scrappedPosts = posts.stream()
        .filter(p -> p.getDeletedAt() == null)
        .filter(p -> scrappedPostIds.contains(p.getId()))
        .filter(p -> cursor == null || p.getId() < cursor)
        .filter(p -> {
          boolean isReported = reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
              memberId, ReportTargetType.POST, p.getId()
          );
          boolean isBlocked = memberBlockRepository.existsByBlockerIdAndBlockedId(
              memberId, p.getMember().getId()
          );
          return !isReported && !isBlocked;
        })
        .sorted((a, b) -> b.getId().compareTo(a.getId()))
        .limit(size + 1)
        .map(p -> {

          // 썸네일 조회 (orderIndex = 1)
          String thumbnailUrl = postImageRepository.findFirstByPostIdAndOrderIndex(p.getId(), 1)
              .map(PostImage::getImageUrl)
              .orElse(null);

          // 전체 이미지 개수
          int imageCount = postImageRepository.findAllByPostId(p.getId()).size();

          return PostResponseDTO.PostListItem.builder()
              .postId(p.getId())
              .category(p.getCategory())
              .categoryLabel(p.getCategory().getLabel())
              .title(p.getTitle())
              .content(p.getContent())
              .likeCount(p.getLikeCount())
              .commentCount(p.getCommentCount())
              .thumbnailUrl(thumbnailUrl)
              .imageCount(imageCount)
              .createdAt(p.getCreatedAt())
              .build();
        })
        .toList();

    boolean hasNext = scrappedPosts.size() > size;
    List<PostResponseDTO.PostListItem> content = hasNext
        ? scrappedPosts.subList(0, size)
        : scrappedPosts;

    Long nextCursor = hasNext && !content.isEmpty()
        ? content.get(content.size() - 1).getPostId()
        : null;

    return CursorPageResponse.<PostResponseDTO.PostListItem>builder()
        .content(content)
        .hasNext(hasNext)
        .nextCursor(nextCursor)
        .build();
  }
}