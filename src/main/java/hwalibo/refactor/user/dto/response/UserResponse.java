package hwalibo.refactor.user.dto.response;

import hwalibo.refactor.user.dto.result.UserResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String profile;
    private int rate;
    private int numReview;

    public static UserResponse from(UserResult result) {
        return new UserResponse(
                result.getId(),
                result.getName(),
                result.getProfile(),
                result.getRate(),
                result.getNumReview()
        );
    }
}
