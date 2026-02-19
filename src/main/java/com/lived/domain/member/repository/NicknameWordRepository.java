package com.lived.domain.member.repository;

import com.lived.domain.member.entity.NicknameWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NicknameWordRepository extends JpaRepository<NicknameWord, Long> {

    @Query(value = "SELECT * FROM nickname_word WHERE type = :type ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<NicknameWord> findRandomByType(@Param("type") String type, @Param("count") int count);
}
