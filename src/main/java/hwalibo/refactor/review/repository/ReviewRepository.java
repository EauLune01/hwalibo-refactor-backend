package hwalibo.refactor.review.repository;

import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.repository.custom.ReviewRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    @Query("SELECT r FROM Review r JOIN FETCH r.toilet WHERE r.id = :reviewId")
    Optional<Review> findWithToiletById(@Param("reviewId") Long reviewId);

    List<Review> findByToiletIdOrderByCreatedAtDesc(Long toiletId, Pageable pageable);

    @EntityGraph(attributePaths = {"toilet"})
    Optional<Review> findById(Long reviewId);

    @EntityGraph(attributePaths = {"toilet"})
    Slice<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
