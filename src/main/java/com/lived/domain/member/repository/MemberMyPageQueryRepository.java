package com.lived.domain.member.repository;

import com.lived.domain.member.entity.Member;
import com.lived.domain.post.entity.Post;
import com.lived.domain.routine.entity.RoutineFruit;
import com.lived.domain.routine.entity.enums.FruitType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MemberMyPageQueryRepository {

    private final EntityManager em;

    // 자신이 가지고 있는 열매 중 gold, normal, growing 우선순으로 5개 가져오기
    public List<RoutineFruit> findTop5Fruits(Member member) {
        return em.createQuery(
                        "SELECT rf FROM RoutineFruit rf " +
                                "WHERE rf.memberRoutine.member = :member " +
                                "AND rf.fruitType IN :types " +
                                "ORDER BY " +
                                "  CASE rf.fruitType " +
                                "    WHEN :gold THEN 1 " +
                                "    WHEN :normal THEN 2 " +
                                "    WHEN :growing THEN 3 " +
                                "    ELSE 4 " +
                                "  END ASC, " +
                                "  rf.createdAt DESC", RoutineFruit.class)
                .setParameter("member", member)
                .setParameter("types", Arrays.asList(FruitType.GOLD, FruitType.NORMAL, FruitType.GROWING))
                .setParameter("gold", FruitType.GOLD)
                .setParameter("normal", FruitType.NORMAL)
                .setParameter("growing", FruitType.GROWING)
                .setMaxResults(5)
                .getResultList();
    }
}