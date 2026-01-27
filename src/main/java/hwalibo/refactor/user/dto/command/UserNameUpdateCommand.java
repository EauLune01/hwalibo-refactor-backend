package hwalibo.refactor.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserNameUpdateCommand {
    private Long userId;
    private String newName;

    public static UserNameUpdateCommand of(Long userId, String newName) {
        return UserNameUpdateCommand.builder()
                .userId(userId)
                .newName(newName)
                .build();
    }
}
