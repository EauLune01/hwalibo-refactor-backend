package hwalibo.refactor.global.auth.dto.command;

import hwalibo.refactor.global.auth.jwt.JwtConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueTokenCommand {
    private final String accessToken;
    private final String refreshToken;

    public static ReissueTokenCommand of(String accessToken, String refreshToken) {
        return new ReissueTokenCommand(accessToken, refreshToken);
    }
}
