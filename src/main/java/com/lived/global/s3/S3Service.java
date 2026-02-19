package com.lived.global.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

  private final S3Client s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Value("${cloud.aws.region.static}")
  private String region;

  /**
   * 게시글 이미지 업로드
   * @param file 업로드할 파일
   * @param postId 게시글 ID
   * @return S3 URL
   */
  public String uploadPostImage(MultipartFile file, Long postId) {
    String fileName = createPostImagePath(postId, file.getOriginalFilename());
    return uploadFile(file, fileName);
  }

  /**
   * 댓글 이미지 업로드
   * @param file 업로드할 파일
   * @param commentId 댓글 ID
   * @return S3 URL
   */
  public String uploadCommentImage(MultipartFile file, Long commentId) {
    String fileName = createCommentImagePath(commentId, file.getOriginalFilename());
    return uploadFile(file, fileName);
  }

  /**
   * 실제 S3 업로드 로직
   */
  private String uploadFile(MultipartFile file, String fileName) {
    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucket)
          .key(fileName)
          .contentType(file.getContentType())
          .build();

      s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

      return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);

    } catch (IOException e) {
      log.error("S3 파일 업로드 실패: {}", e.getMessage());
      throw new RuntimeException("S3 파일 업로드 실패", e);
    }
  }

  /**
   * 파일 삭제
   */
  public void deleteFile(String fileUrl) {
    String fileName = extractFileNameFromUrl(fileUrl);

    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(bucket)
        .key(fileName)
        .build();

    s3Client.deleteObject(deleteObjectRequest);
    log.info("S3 파일 삭제 완료: {}", fileName);
  }

  /**
   * 게시글 이미지 경로 생성: posts/{postId}/{uuid}-{originalFileName}
   */
  private String createPostImagePath(Long postId, String originalFileName) {
    String uuid = UUID.randomUUID().toString();
    return String.format("posts/%d/%s-%s", postId, uuid, originalFileName);
  }

  /**
   * 댓글 이미지 경로 생성: comments/{commentId}/{uuid}-{originalFileName}
   */
  private String createCommentImagePath(Long commentId, String originalFileName) {
    String uuid = UUID.randomUUID().toString();
    return String.format("comments/%d/%s-%s", commentId, uuid, originalFileName);
  }

  /**
   * URL에서 파일명 추출
   */
  private String extractFileNameFromUrl(String fileUrl) {
    // https://liveds3.s3.ap-northeast-2.amazonaws.com/posts/1/uuid-image.jpg
    // → posts/1/uuid-image.jpg
    return fileUrl.substring(fileUrl.indexOf(".com/") + 5);
  }

  /**
   * 멤버 프로필 이미지 업로드
   */
  public String uploadMemberImage(MultipartFile file, Long memberId) {
    String fileName = createMemberImagePath(memberId, file.getOriginalFilename());
    return uploadFile(file, fileName);
  }

  /**
   * 멤버 이미지 경로 생성: members/{memberId}/{uuid}-{originalFileName}
   */
  private String createMemberImagePath(Long memberId, String originalFileName) {
    String uuid = UUID.randomUUID().toString();
    return String.format("members/%d/%s-%s", memberId, uuid, originalFileName);
  }
}