package hwalibo.refactor.global.auth.dto.command;

import hwalibo.refactor.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WithdrawCommand {
    private User user;
    private String accessToken;

    public static WithdrawCommand of(User user, String accessToken) {
        return new WithdrawCommand(user, accessToken);
    }
}
