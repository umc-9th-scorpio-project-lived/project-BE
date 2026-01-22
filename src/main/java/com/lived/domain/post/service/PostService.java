package com.lived.domain.post.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.post.converter.PostConverter;
import com.lived.domain.post.dto.PostRequestDTO;
import com.lived.domain.post.dto.PostResponseDTO;
import com.lived.domain.post.entity.Post;
import com.lived.domain.post.entity.PostImage;
import com.lived.domain.post.entity.mapping.PostLike;
import com.lived.domain.post.entity.mapping.PostScrap;
import com.lived.domain.post.repository.PostImageRepository;
import com.lived.domain.post.repository.PostLikeRepository;
import com.lived.domain.post.repository.PostRepository;
import com.lived.domain.post.repository.PostScrapRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import com.lived.global.s3.S3Service;
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
    Optional<PostScrap> existingScrap = postScrapRepository.findByPostIdAndMemberId(postId, memberId);

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
}