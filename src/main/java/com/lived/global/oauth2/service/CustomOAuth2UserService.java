package com.lived.global.oauth2.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.enums.MemberStatus;
import com.lived.domain.member.enums.Provider;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.notification.entity.NotificationSetting;
import com.lived.domain.notification.repository.NotificationSettingRepository;
import com.lived.global.oauth2.dto.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //기본 서버로부터 유저 정보를 가져옴
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //어떤 소셜 서비스인지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        //DB 조회를 통해 신규 유저인지 판단
        Provider providerEnum = Provider.valueOf(attributes.getProvider().toUpperCase());

        Optional<Member> memberEntity = memberRepository.findBySocialIdAndProvider(
                attributes.getSocialId(),
                providerEnum
        );

        Member member;
        boolean isNewMember = memberEntity.isEmpty();

        // 만약 INACTIVE인 유저라면 복구 수행
        if (memberEntity.isEmpty()) {
            member = memberRepository.save(attributes.toEntity(providerEnum));
            createNotificationSetting(member);
            isNewMember = true;
        } else {
            member = memberEntity.get();
            isNewMember = false;

            if (MemberStatus.INACTIVE.equals(member.getStatus())) {
                member.recover();
            }

            if (notificationSettingRepository.findByMember(member).isEmpty()) {
                createNotificationSetting(member);
            }
        }

        Map<String, Object> memberData = new HashMap<>(attributes.getAttributes());
        memberData.put("isNewMember", isNewMember);
        memberData.put("name", attributes.getName());
        memberData.put("email", attributes.getEmail());
        memberData.put("socialId", attributes.getSocialId());
        memberData.put("provider", attributes.getProvider());

        if (!isNewMember) {
            memberData.put("memberId", member.getId());
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                memberData,
                attributes.getNameAttributeKey()
        );
    }

    private void createNotificationSetting(Member member) {
        NotificationSetting setting = NotificationSetting.builder()
                .member(member)
                .allEnabled(true)
                .routineEnabled(true)
                .routineReportEnabled(true)
                .communityEnabled(true)
                .communityHotEnabled(true)
                .commentEnabled(true) // 엔티티 필드에 맞춰 추가
                .marketingEnabled(false)
                .build();

        notificationSettingRepository.save(setting);
    }
}