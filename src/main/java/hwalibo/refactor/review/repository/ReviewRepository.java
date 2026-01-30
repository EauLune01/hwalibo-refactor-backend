package hwalibo.refactor.review.repository;

import hwalibo.refactor.review.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
            "WHERE r.toilet.id = :toiletId " +
            "ORDER BY r.createdAt DESC")
    List<Review> findLatestReviews(@Param("toiletId") Long toiletId, Pageable pageable);

    @Query("select distinct r from Review r " +
            "left join fetch r.reviewImages " +
            "where r.id = :reviewId")
    Optional<Review> findReviewWithImages(@Param("reviewId") Long reviewId);

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.toilet " +
            "WHERE r.user.id = :userId")
    Slice<Review> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.toilet WHERE r.id = :id")
    Optional<Review> findWithToiletById(@Param("id") Long id);
}
