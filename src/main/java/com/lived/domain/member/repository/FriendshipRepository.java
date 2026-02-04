package com.lived.domain.member.repository;

import com.lived.domain.member.entity.Friendship;
import com.lived.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f " +
            "JOIN FETCH f.requester " +
            "JOIN FETCH f.receiver " +
            "WHERE (f.requester = :member OR f.receiver = :member) " +
            "AND f.status = 'ACCEPTED' " +
            "AND f.isDeleted = false")
    List<Friendship> findAllAcceptedFriends(@Param("member") Member member);

    // 이미 존재하는 친구 관계인지 확인
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE " +
            "((f.requester = :m1 AND f.receiver = :m2) OR (f.requester = :m2 AND f.receiver = :m1)) " +
            "AND f.isDeleted = false")
    boolean existsByRequesterAndReceiver(@Param("m1") Member m1, @Param("m2") Member m2);
}
