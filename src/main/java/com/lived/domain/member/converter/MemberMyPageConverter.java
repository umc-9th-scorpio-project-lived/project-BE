package com.lived.domain.member.converter;

import com.lived.domain.member.dto.MemberMyPageResponseDTO;
import com.lived.domain.member.entity.Member;
import com.lived.domain.post.dto.PostResponseDTO;
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
                .profileImageUrl(member.getProfileImageUrl())
                .livingPeriod(member.getLivingPeriod().name())
                .fruits(mapToFruitInfoList(fruits))
                .build();
    }

    // 게시글 리스트 변환
    public MemberMyPageResponseDTO.CommunityProfilePostListResponse toMemberPostListResponse(List<PostResponseDTO.PostListItem> postList) {

        List<MemberMyPageResponseDTO.MyPagePostPreview> previewList = postList.stream()
                .map(this::toMyPagePostPreview)
                .collect(Collectors.toList());

        return MemberMyPageResponseDTO.CommunityProfilePostListResponse.builder()
                .posts(previewList)
                .build();
    }

    private MemberMyPageResponseDTO.MyPagePostPreview toMyPagePostPreview(PostResponseDTO.PostListItem postItem) {
        return MemberMyPageResponseDTO.MyPagePostPreview.builder()
                .postId(postItem.getPostId())
                .category(postItem.getCategoryLabel())
                .title(postItem.getTitle())
                .contentSummary(postItem.getContent())
                .likeCount(postItem.getLikeCount())
                .commentCount(postItem.getCommentCount())
                .createdAt(postItem.getCreatedAt().toString())
                .firstImageUrl(postItem.getThumbnailUrl())
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