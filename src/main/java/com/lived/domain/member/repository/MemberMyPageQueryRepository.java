package com.lived.domain.member.repository;

import com.lived.domain.member.entity.Member;
import com.lived.domain.routine.entity.RoutineBigFruit;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberMyPageQueryRepository {

    private final EntityManager em;

    /**
     * 사용자가 보유한 대형 열매(RoutineBigFruit) 중 최근 획득한 순서대로 최대 5개를 조회합니다.
     */
    public List<RoutineBigFruit> findTop5BigFruits(Member member) {
        return em.createQuery(
                        "SELECT rbf FROM RoutineBigFruit rbf " +
                                "WHERE rbf.member = :member " +
                                "ORDER BY rbf.createdAt DESC", RoutineBigFruit.class)
                .setParameter("member", member)
                .setMaxResults(5)
                .getResultList();
    }
}