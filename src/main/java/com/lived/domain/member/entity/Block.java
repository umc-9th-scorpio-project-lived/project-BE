// MemberBlock.java
package com.lived.domain.member.entity;

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
    name = "member_block",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_member_block_blocker_blocked",
            columnNames = {"blocker_id", "blocked_id"}
        )
    }
)
public class Block extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_block_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "blocker_id", nullable = false)
  private Member blocker;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "blocked_id", nullable = false)
  private Member blocked;
}