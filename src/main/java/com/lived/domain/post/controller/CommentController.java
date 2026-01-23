package com.lived.domain.post.controller;

import com.lived.domain.post.dto.CommentRequestDTO;
import com.lived.domain.post.dto.CommentResponseDTO;
import com.lived.domain.post.service.CommentService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
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
      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestHeader("Member-Id") Long memberId,

      @Parameter(description = "게시글 ID", required = true, example = "123")
      @PathVariable Long postId,

      @Parameter(description = "댓글 작성 요청 데이터", required = true)
      @Valid @RequestBody CommentRequestDTO.CreateCommentRequest request
  ) {
    CommentResponseDTO.CreateCommentResponse response =
        commentService.createComment(postId, memberId, request);
    return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, response);
  }
}