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

    // 자신이 작성한 글 조회
    public List<Post> findWrittenPosts(Member member) {
        return em.createQuery(
                        "SELECT p FROM Post p WHERE p.member = :member AND p.deletedAt IS NULL ORDER BY p.createdAt DESC", Post.class)
                .setParameter("member", member)
                .getResultList();
    }

    // 자신이 댓글 단 게시글의 ID 조회
    public List<Long> findCommentedPostIds(Member member) {
        return em.createQuery(
                        "SELECT DISTINCT c.post.id FROM Comment c WHERE c.member = :member AND c.deletedAt IS NULL", Long.class)
                .setParameter("member", member)
                .getResultList();
    }

    // 자신이 스크랩한 게시글의 ID 조회
    public List<Long> findScrappedPostIds(Member member) {
        return em.createQuery(
                        "SELECT s.post.id FROM PostScrap s WHERE s.member = :member", Long.class)
                .setParameter("member", member)
                .getResultList();
    }

    // ID 리스트로 게시글 일괄 조회
    public List<Post> findPostsByIds(List<Long> postIds) {
        if (postIds.isEmpty()) return List.of();
        return em.createQuery(
                        "SELECT p FROM Post p WHERE p.id IN :postIds AND p.deletedAt IS NULL ORDER BY p.createdAt DESC", Post.class)
                .setParameter("postIds", postIds)
                .getResultList();
    }

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

    public Map<Long, String> findThumbnailsByPostIds(List<Long> postIds) {
        if (postIds.isEmpty())
            return new HashMap<>();

        // 각 게시글의 orderIndex가 0인 이미지만 조회
        List<Object[]> results = em.createQuery(
                        "SELECT pi.post.id, pi.imageUrl FROM PostImage pi " +
                                "WHERE pi.post.id IN :postIds AND pi.orderIndex = 0", Object[].class)
                .setParameter("postIds", postIds)
                .getResultList();

        // [PostID : ImageURL] 형태의 Map으로 변환
        return results.stream().collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (String) row[1]
        ));
    }
}