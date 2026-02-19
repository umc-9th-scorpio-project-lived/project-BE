package com.lived.domain.routine.entity;

import com.lived.domain.routine.enums.CategoryName;
import com.lived.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RoutineCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryName name;

    @Column(nullable = false, length = 20)
    private String emoji;
}
