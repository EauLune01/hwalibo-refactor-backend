package hwalibo.refactor.global.auth.service;

import hwalibo.refactor.global.auth.CustomOAuth2User;
import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.global.domain.UserStatus;
import hwalibo.refactor.global.oauth2.provider.NaverUserInfo;
import hwalibo.refactor.global.oauth2.provider.OAuth2UserInfo;
import hwalibo.refactor.user.domain.User;
import hwalibo.refactor.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo;
        if ("naver".equals(registrationId)) {
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        Gender genderEnum = oAuth2UserInfo.getGender();

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .map(existingUser -> {
                    // 1. 탈퇴한 유저라면 이름까지 업데이트하며 복구
                    if (existingUser.getStatus() == UserStatus.DELETED) {
                        existingUser.reActivate();
                        existingUser.updateName(oAuth2UserInfo.getName());
                    }

                    // 2. 이미 ACTIVE거나 방금 복구된 유저 모두 최신 정보로 갱신
                    existingUser.updateProfileImage(oAuth2UserInfo.getProfileImageUrl());
                    existingUser.updateGender(genderEnum);

                    return existingUser;
                })
                .orElseGet(() -> saveNewUser(oAuth2UserInfo));

        return new CustomOAuth2User(user, oAuth2UserInfo.getAttributes());
    }

    private User saveNewUser(OAuth2UserInfo oAuth2UserInfo) {
        User newUser = User.create(
                oAuth2UserInfo.getProvider(),
                oAuth2UserInfo.getProviderId(),
                oAuth2UserInfo.getName(),
                oAuth2UserInfo.getProfileImageUrl(),
                oAuth2UserInfo.getGender()
        );

        return userRepository.save(newUser);
    }
}
