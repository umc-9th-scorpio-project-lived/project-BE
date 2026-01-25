package com.lived.domain.member.converter;

import com.lived.domain.member.dto.MemberMyPageResponseDTO;
import com.lived.domain.member.entity.Member;
import com.lived.domain.post.entity.Post;
import com.lived.domain.routine.entity.RoutineFruit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MemberMyPageConverter {

    // 기본 프로필 정보 변환
    public MemberMyPageResponseDTO.MyProfileResponse toMemberProfileResponse(Member member) {
        return MemberMyPageResponseDTO.MyProfileResponse.builder()
                .name(member.getName())
                .build();
    }

    // 커뮤니티 프로필 정보 변환
    public MemberMyPageResponseDTO.CommunityProfileResponse toCommunityProfileResponse(Member member, List<RoutineFruit> fruits) {
        return MemberMyPageResponseDTO.CommunityProfileResponse.builder()
                .nickname(member.getNickname())
                .livingPeriod(member.getLivingPeriod().name())
                .fruits(mapToFruitInfoList(fruits))
                .build();
    }

    // 게시글 리스트 변환
    public MemberMyPageResponseDTO.CommunityProfilePostListResponse toMemberPostListResponse(List<Post> posts, Map<Long, String> thumbnails) {

        List<MemberMyPageResponseDTO.MyPagePostPreview> previewList = posts.stream()
                .map(post -> toMyPagePostPreview(post, thumbnails.get(post.getId())))
                .collect(Collectors.toList());

        return MemberMyPageResponseDTO.CommunityProfilePostListResponse.builder()
                .posts(previewList)
                .build();
    }

    private MemberMyPageResponseDTO.MyPagePostPreview toMyPagePostPreview(Post post, String thumbnailUrl) {
        return MemberMyPageResponseDTO.MyPagePostPreview.builder()
                .postId(post.getId())
                .category(post.getCategory() != null ? post.getCategory().getLabel() : "기타")
                .title(post.getTitle())
                .contentSummary(post.getContent().length() > 50 ? post.getContent().substring(0, 50) + "..." : post.getContent())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt().toString())
                .firstImageUrl(thumbnailUrl) // 전달받은 썸네일 URL 사용
                .build();
    }

    private List<MemberMyPageResponseDTO.MyPageFruitInfo> mapToFruitInfoList(List<RoutineFruit> fruits) {
        return fruits.stream()
                .map(fruit -> MemberMyPageResponseDTO.MyPageFruitInfo.builder()
                        .fruitId(fruit.getId())
                        .fruitType(fruit.getFruitType().name())
                        .build())
                .collect(Collectors.toList());
    }
}