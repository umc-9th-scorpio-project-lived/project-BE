package com.lived.domain.member.service;

import com.lived.domain.member.converter.MemberMyPageConverter;
import com.lived.domain.member.dto.MemberMyPageResponseDTO;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberMyPageQueryRepository;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.post.entity.Post;
import com.lived.domain.routine.entity.RoutineFruit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberMyPageService {

    private final MemberRepository memberRepository;
    private final MemberMyPageQueryRepository queryRepository;
    private final MemberMyPageConverter memberConverter;

    // 1. 커뮤니티 프로필 정보 조회
    public MemberMyPageResponseDTO.CommunityProfileResponse getCommunityProfile(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        List<RoutineFruit> fruits = queryRepository.findTop5Fruits(member);
        return memberConverter.toCommunityProfileResponse(member, fruits);
    }

    // 2. 작성한 글 조회
    public MemberMyPageResponseDTO.CommunityProfilePostListResponse getWrittenPosts(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        List<Post> posts = queryRepository.findWrittenPosts(member);
        return getPostListResponse(posts);
    }

    // 3. 댓글 단 글 조회
    public MemberMyPageResponseDTO.CommunityProfilePostListResponse getCommentedPosts(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        List<Long> postIds = queryRepository.findCommentedPostIds(member);
        List<Post> posts = queryRepository.findPostsByIds(postIds);
        return getPostListResponse(posts);
    }

    // 4. 스크랩한 글 조회
    public MemberMyPageResponseDTO.CommunityProfilePostListResponse getScrappedPosts(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        List<Long> postIds = queryRepository.findScrappedPostIds(member);
        List<Post> posts = queryRepository.findPostsByIds(postIds);
        return getPostListResponse(posts);
    }

    // 공통 썸네일 추출 로직
    private MemberMyPageResponseDTO.CommunityProfilePostListResponse getPostListResponse(List<Post> posts) {
        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
        Map<Long, String> thumbnails = queryRepository.findThumbnailsByPostIds(postIds);
        return memberConverter.toMemberPostListResponse(posts, thumbnails);
    }
}
