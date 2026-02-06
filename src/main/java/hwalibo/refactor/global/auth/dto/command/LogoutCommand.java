package hwalibo.refactor.global.auth.dto.command;

import hwalibo.refactor.global.auth.jwt.JwtConstants;
import hwalibo.refactor.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogoutCommand {
    private User user;
    private String accessToken;

    public static LogoutCommand of(User user, String accessToken) {
        return new LogoutCommand(user, accessToken);
    }
}
