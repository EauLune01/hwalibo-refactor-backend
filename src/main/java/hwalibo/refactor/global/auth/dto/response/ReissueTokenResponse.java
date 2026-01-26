package hwalibo.refactor.global.auth.dto.response;

import hwalibo.refactor.global.auth.dto.result.ReissueTokenResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueTokenResponse {

    private final String accessToken;
    private final String refreshToken;


    public static ReissueTokenResponse from(ReissueTokenResult result) {
        return new ReissueTokenResponse(result.getAccessToken(), result.getRefreshToken());
    }
}
