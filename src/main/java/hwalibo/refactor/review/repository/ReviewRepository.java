package hwalibo.refactor.review.repository;

import hwalibo.refactor.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
