package hwalibo.refactor.reviewLike.repository;

import hwalibo.refactor.reviewLike.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

    Optional<ReviewLike> findByReviewIdAndUserId(Long reviewId, Long userId);

    @Query("select rl.review.id from ReviewLike rl where rl.user.id = :userId")
    List<Long> findReviewIdsByUserId(@Param("userId") Long userId);
}
