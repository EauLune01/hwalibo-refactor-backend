package hwalibo.refactor.global.oauth2.provider;


import hwalibo.refactor.global.domain.Gender;

import java.util.Map;

public interface OAuth2UserInfo {

    // 제공자(구글, 네이버 등)의 고유 ID를 반환
    String getProviderId();

    // 제공자 이름 반환
    String getProvider();

    // 사용자 이름(닉네임) 반환
    String getName();

    // 사용자 프로필 이미지 URL 반환
    String getProfileImageUrl();

    //사용자의 성별 정보 반환
    Gender getGender();

    // 사용자 정보가 담긴 원본 Map 데이터 반환
    Map<String, Object> getAttributes();
}
