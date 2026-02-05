package com.lived.domain.member.repository;

import com.lived.domain.member.entity.Friendship;
import com.lived.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    // 친구 목록 조회
    @Query("SELECT f FROM Friendship f " +
            "JOIN FETCH f.requester " +
            "JOIN FETCH f.receiver " +
            "WHERE (f.requester = :member OR f.receiver = :member) " +
            "AND f.status = 'ACCEPTED' " +
            "AND f.isDeleted = false")
    List<Friendship> findAllAcceptedFriends(@Param("member") Member member);

    // 두 사람 사이의 모든 관계 조회
    @Query("SELECT f FROM Friendship f WHERE " +
            "((f.requester.id = :m1Id AND f.receiver.id = :m2Id) OR " +
            " (f.requester.id = :m2Id AND f.receiver.id = :m1Id))")
    Optional<Friendship> findAnyFriendship(@Param("m1Id") Long m1Id, @Param("m2Id") Long m2Id);

    // 이미 존재하는 친구 관계인지 확인
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE " +
            "((f.requester.id = :m1Id AND f.receiver.id = :m2Id) OR " +
            " (f.requester.id = :m2Id AND f.receiver.id = :m1Id)) " +
            "AND f.status = 'ACCEPTED' AND f.isDeleted = false")
    boolean existsFriendship(@Param("m1Id") Long m1Id, @Param("m2Id") Long m2Id);

    // 두 사람 사이의 활성화된 친구 관계를 찾기
    @Query("SELECT f FROM Friendship f WHERE " +
            "((f.requester.id = :m1Id AND f.receiver.id = :m2Id) OR " +
            " (f.requester.id = :m2Id AND f.receiver.id = :m1Id)) " +
            "AND f.status = 'ACCEPTED' AND f.isDeleted = false")
    Optional<Friendship> findActiveFriendship(@Param("m1Id") Long m1Id, @Param("m2Id") Long m2Id);
}
