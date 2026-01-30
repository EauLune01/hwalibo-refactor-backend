package hwalibo.refactor.reviewLike.repository;

import hwalibo.refactor.reviewLike.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    @Query("SELECT COUNT(rl) > 0 FROM ReviewLike rl " +
            "WHERE rl.review.id = :reviewId AND rl.user.id = :userId")
    boolean existsByReviewIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    @Query("SELECT rl FROM ReviewLike rl " +
            "WHERE rl.review.id = :reviewId AND rl.user.id = :userId")
    Optional<ReviewLike> findByReviewIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
}
