package hwalibo.refactor.reviewLike.repository;

import hwalibo.refactor.reviewLike.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike,Long> {
}
