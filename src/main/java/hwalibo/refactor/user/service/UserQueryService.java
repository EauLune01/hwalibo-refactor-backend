package hwalibo.refactor.user.service;

import hwalibo.refactor.global.domain.UserStatus;
import hwalibo.refactor.global.exception.auth.UnauthorizedException;
import hwalibo.refactor.global.exception.user.UserNotFoundException;
import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.dto.response.ReviewResponse;
import hwalibo.refactor.review.repository.ReviewRepository;
import hwalibo.refactor.user.domain.User;
import hwalibo.refactor.user.dto.result.UserResult;
import hwalibo.refactor.user.dto.result.UserReviewResult;
import hwalibo.refactor.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserCacheService userCacheService;
    private final ReviewRepository reviewRepository;

    public UserResult getUserInfo(Long userId) {
        User user = validateAndGetActiveUser(userId);
        int rate = userCacheService.calculateUserRate(user.getId());
        return UserResult.from(user, rate);
    }

    @Transactional(readOnly = true)
    public Slice<UserReviewResult> getUserReviews(Long userId, Pageable pageable) {
        Slice<Review> reviewSlice = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return reviewSlice.map(UserReviewResult::from);
    }

    /******************** Helper Method ********************/

    private User validateAndGetActiveUser(Long userId) {
        return userRepository.findById(userId)
                .filter(user -> user.getStatus() == UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없거나 활성 상태가 아닙니다."));
    }

}
