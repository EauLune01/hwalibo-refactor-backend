package hwalibo.refactor.user.dto.result;

import hwalibo.refactor.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResult {
    private Long id;
    private String name;
    private String profile;
    private int rate;
    private int numReview;

    public static UserResult from(User user, int rate) {
        return new UserResult(
                user.getId(),
                user.getName(),
                user.getProfile(),
                rate,
                user.getNumReview() != null ? user.getNumReview() : 0
        );
    }
}
