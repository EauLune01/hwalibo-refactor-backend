package hwalibo.refactor.user.dto.result;

import hwalibo.refactor.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResult {
    private Long id;
    private String name;
    private String profile;
    private int rate;
    private int numReview;

    public static UserResult from(User user, int rate) {
        return UserResult.builder()
                .id(user.getId())
                .name(user.getName())
                .profile(user.getProfile())
                .rate(rate)
                .numReview(user.getNumReview() != null ? user.getNumReview() : 0)
                .build();
    }
}
