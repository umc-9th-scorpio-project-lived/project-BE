package com.lived.domain.post.entity;

import com.lived.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
    name = "post_image",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_post_image_post_order",
            columnNames = {"post_id", "order_index"}
        )
    }
)
public class PostImage extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "post_image_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Column(name = "order_index", nullable = false)
  private Integer orderIndex;

  public void updateOrderIndex(Integer orderIndex) {
    this.orderIndex = orderIndex;
  }
}