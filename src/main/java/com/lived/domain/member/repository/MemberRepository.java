package com.lived.domain.member.repository;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 기존 유저 로그인용 (소셜 정보로 멤버 객체 찾아오기)
    Optional<Member> findBySocialIdAndProvider(String socialId, Provider provider);

}
