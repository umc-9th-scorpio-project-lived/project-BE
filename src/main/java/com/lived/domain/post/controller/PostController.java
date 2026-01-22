package com.lived.domain.post.controller;

import com.lived.domain.post.dto.PostRequestDTO;
import com.lived.domain.post.dto.PostResponseDTO;
import com.lived.domain.post.service.PostService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Community")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  @Operation(
      summary = "게시글 작성",
      description = "새로운 게시글을 작성합니다. 이미지는 최대 10개까지 첨부 가능합니다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "201",
          description = "게시글 작성 성공"
      )
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<PostResponseDTO.CreatePostResponse> createPost(
      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestHeader("Member-Id") Long memberId,

      @Parameter(description = "게시글 작성 요청 데이터", required = true)
      @Valid @ModelAttribute PostRequestDTO.CreatePostRequest request,

      @Parameter(description = "첨부 이미지 파일 목록 (최대 10개)", required = false)
      @RequestPart(value = "images", required = false) List<MultipartFile> images
  ) {
    PostResponseDTO.CreatePostResponse response = postService.createPost(request, images, memberId);
    return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, response);
  }

  @Operation(
      summary = "게시글 수정",
      description = "게시글을 수정합니다. 카테고리, 제목, 본문, 이미지를 수정할 수 있습니다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "게시글 수정 성공"
      )
  })
  @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<PostResponseDTO.UpdatePostResponse> updatePost(
      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestHeader("Member-Id") Long memberId,

      @Parameter(description = "게시글 ID", required = true, example = "1")
      @PathVariable Long postId,

      @Parameter(description = "게시글 수정 요청 데이터", required = true)
      @Valid @ModelAttribute PostRequestDTO.UpdatePostRequest request,

      @Parameter(description = "새로 추가할 이미지 파일 목록", required = false)
      @RequestPart(value = "images", required = false) List<MultipartFile> images
  ) {
    PostResponseDTO.UpdatePostResponse response = postService.updatePost(postId, memberId, request, images);
    return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
  }
}