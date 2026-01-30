package com.lived.domain.post.controller;

import com.lived.domain.post.dto.CommentRequestDTO;
import com.lived.domain.post.dto.CommentResponseDTO;
import com.lived.domain.post.service.CommentService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import com.lived.global.auth.annotation.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Community")
@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @Operation(
      summary = "댓글 작성",
      description = "게시글에 댓글을 작성합니다. 대댓글 작성 시 parentCommentId를 포함합니다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "201",
          description = "댓글 작성 성공"
      )
  })
  @PostMapping
  public ApiResponse<CommentResponseDTO.CreateCommentResponse> createComment(
      @Parameter(hidden = true) @AuthMember Long memberId,

      @Parameter(description = "게시글 ID", required = true, example = "123")
      @PathVariable Long postId,

      @Parameter(description = "댓글 작성 요청 데이터", required = true)
      @Valid @RequestBody CommentRequestDTO.CreateCommentRequest request
  ) {
    CommentResponseDTO.CreateCommentResponse response =
        commentService.createComment(postId, memberId, request);
    return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, response);
  }

  @Operation(
      summary = "댓글 수정",
      description = "댓글 내용을 수정합니다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "댓글 수정 성공"
      )
  })
  @PatchMapping("/{commentId}")
  public ApiResponse<CommentResponseDTO.UpdateCommentResponse> updateComment(
      @Parameter(hidden = true) @AuthMember Long memberId,

      @Parameter(description = "게시글 ID", required = true, example = "123")
      @PathVariable Long postId,

      @Parameter(description = "댓글 ID", required = true, example = "78")
      @PathVariable Long commentId,

      @Parameter(description = "댓글 수정 요청 데이터", required = true)
      @Valid @RequestBody CommentRequestDTO.UpdateCommentRequest request
  ) {
    CommentResponseDTO.UpdateCommentResponse response =
        commentService.updateComment(postId, commentId, memberId, request);
    return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
  }

  @Operation(
      summary = "댓글 삭제",
      description = "댓글을 삭제합니다. (Soft Delete)"
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "댓글 삭제 성공"
      )
  })
  @DeleteMapping("/{commentId}")
  public ApiResponse<CommentResponseDTO.DeleteCommentResponse> deleteComment(
      @Parameter(hidden = true) @AuthMember Long memberId,

      @Parameter(description = "게시글 ID", required = true, example = "123")
      @PathVariable Long postId,

      @Parameter(description = "댓글 ID", required = true, example = "78")
      @PathVariable Long commentId
  ) {
    CommentResponseDTO.DeleteCommentResponse response =
        commentService.deleteComment(postId, commentId, memberId);
    return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
  }

  @Operation(
      summary = "댓글 좋아요 토글",
      description = "댓글 좋아요를 추가하거나 취소합니다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "좋아요 토글 성공"
      )
  })
  @PostMapping("/{commentId}/like")
  public ApiResponse<CommentResponseDTO.ToggleLikeResponse> toggleCommentLike(
      @Parameter(hidden = true) @AuthMember Long memberId,

      @Parameter(description = "게시글 ID", required = true, example = "123")
      @PathVariable Long postId,

      @Parameter(description = "댓글 ID", required = true, example = "78")
      @PathVariable Long commentId
  ) {
    CommentResponseDTO.ToggleLikeResponse response =
        commentService.toggleCommentLike(postId, commentId, memberId);
    return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
  }

  @Operation(
      summary = "댓글 목록 조회",
      description = "게시글의 댓글 목록을 조회합니다. 최상위 댓글은 최신순, 답글은 오래된순으로 정렬됩니다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      )
  })
  @GetMapping
  public ApiResponse<CommentResponseDTO.CommentListResponse> getCommentList(
      @Parameter(hidden = true) @AuthMember Long memberId,

      @Parameter(description = "게시글 ID", required = true, example = "123")
      @PathVariable Long postId,

      @Parameter(description = "커서", required = false)
      @RequestParam(required = false) Long cursor,

      @Parameter(description = "페이지 크기", required = false)
      @RequestParam(defaultValue = "20") int size
  ) {
    CommentResponseDTO.CommentListResponse response =
        commentService.getCommentList(postId, memberId, cursor, size);
    return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
  }
}