package hwalibo.refactor.global.oauth2.provider;

import hwalibo.refactor.global.domain.Gender;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() {
        return (String) attributes.get("nickname");
    }

    @Override
    public String getProfileImageUrl() {
        return (String) attributes.get("profile_image");
    }

    @Override
    public Gender getGender() {
        String naverGenderCode = (String) attributes.get("gender");
        return Gender.fromNaverCode(naverGenderCode);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}
