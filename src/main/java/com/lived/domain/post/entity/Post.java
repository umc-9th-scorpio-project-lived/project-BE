package com.lived.domain.post.entity;

import com.lived.domain.member.entity.Member;
import com.lived.domain.post.dto.PostRequestDTO;
import com.lived.domain.post.entity.enums.PostCategory;
import com.lived.global.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Builder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "post")
public class Post extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "post_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private PostCategory category;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @Builder.Default
  @Column(name = "view_count", nullable = false)
  private Integer viewCount = 0;

  @Builder.Default
  @Column(name = "like_count", nullable = false)
  private Integer likeCount = 0;

  @Builder.Default
  @Column(name = "comment_count", nullable = false)
  private Integer commentCount = 0;

  @Builder.Default
  @Column(name = "scrap_count", nullable = false)
  private Integer scrapCount = 0;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  public void update(PostRequestDTO.UpdatePostRequest request) {
    if (request.getCategory() != null) {
      this.category = request.getCategory();
    }
    if (request.getTitle() != null) {
      this.title = request.getTitle();
    }
    if (request.getContent() != null) {
      this.content = request.getContent();
    }
  }
}