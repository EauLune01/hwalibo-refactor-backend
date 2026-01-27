package hwalibo.refactor.user.service;

import hwalibo.refactor.global.exception.auth.UnauthorizedException;
import hwalibo.refactor.global.exception.user.UserNotFoundException;
import hwalibo.refactor.user.domain.User;
import hwalibo.refactor.user.dto.result.UserResult;
import hwalibo.refactor.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // ✅ 조회 성능 최적화
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserRankService userRankService;

    public UserResult getUserInfo(User loginUser) {
        User user = getAuthenticatedUserOrThrow(loginUser);

        return buildUserResultWithRate(user);
    }
    /******************** Helper Method ********************/

    private User getAuthenticatedUserOrThrow(User loginUser) {
        if (loginUser == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        return userRepository.findById(loginUser.getId())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private UserResult buildUserResultWithRate(User user) {
        int rate = userRankService.calculateUserRate(user.getId());
        return UserResult.from(user, rate);
    }
}
