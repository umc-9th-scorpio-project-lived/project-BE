package com.lived.domain.post.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.post.converter.PostConverter;
import com.lived.domain.post.dto.PostRequestDTO;
import com.lived.domain.post.dto.PostResponseDTO;
import com.lived.domain.post.entity.Post;
import com.lived.domain.post.entity.PostImage;
import com.lived.domain.post.repository.PostImageRepository;
import com.lived.domain.post.repository.PostRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import com.lived.global.s3.S3Service;
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

  private static final int MAX_IMAGE_COUNT = 10;

  @Transactional
  public PostResponseDTO.CreatePostResponse createPost(
      PostRequestDTO.CreatePostRequest request,
      List<MultipartFile> images,
      Long memberId
  ) {
    // 이미지 개수 검증
    if (images != null && images.size() > MAX_IMAGE_COUNT) {
      throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
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
}