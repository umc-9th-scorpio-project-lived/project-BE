package com.lived.domain.post.converter;

import com.lived.domain.member.entity.Member;
import com.lived.domain.post.dto.PostRequestDTO;
import com.lived.domain.post.dto.PostResponseDTO;
import com.lived.domain.post.entity.Post;
import com.lived.domain.post.entity.PostImage;

public class PostConverter {

  // Request → Post Entity
  public static Post toPost(PostRequestDTO.CreatePostRequest request, Member member) {
    return Post.builder()
        .member(member)
        .category(request.getCategory())
        .title(request.getTitle())
        .content(request.getContent())
        .viewCount(0)
        .likeCount(0)
        .commentCount(0)
        .scrapCount(0)
        .build();
  }

  // PostImage Entity 생성
  public static PostImage toPostImage(Post post, String imageUrl, int orderIndex) {
    return PostImage.builder()
        .post(post)
        .imageUrl(imageUrl)
        .orderIndex(orderIndex)
        .build();
  }

  // Post Entity → Response
  public static PostResponseDTO.CreatePostResponse toCreatePostResponse(Post post) {
    return PostResponseDTO.CreatePostResponse.builder()
        .postId(post.getId())
        .build();
  }

  // Post Entity → UpdatePostResponse
  public static PostResponseDTO.UpdatePostResponse toUpdatePostResponse(Post post) {
    return PostResponseDTO.UpdatePostResponse.builder()
        .postId(post.getId())
        .build();
  }

  // Post Entity → DeletePostResponse
  public static PostResponseDTO.DeletePostResponse toDeletePostResponse(Post post) {
    return PostResponseDTO.DeletePostResponse.builder()
        .postId(post.getId())
        .build();
  }
}