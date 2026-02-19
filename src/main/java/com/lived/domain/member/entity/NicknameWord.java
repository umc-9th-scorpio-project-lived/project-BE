package com.lived.domain.member.entity;

import com.lived.domain.member.enums.WordType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NicknameWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WordType type; // ADJECTIVE, NOUN

    @Column(nullable = false, length = 20)
    private String word;

}