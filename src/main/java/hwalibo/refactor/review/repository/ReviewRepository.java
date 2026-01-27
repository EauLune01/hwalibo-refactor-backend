package hwalibo.refactor.review.repository;

import hwalibo.refactor.review.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
            "WHERE r.toilet.id = :toiletId " +
            "ORDER BY r.createdAt DESC")
    List<Review> findLatestReviews(@Param("toiletId") Long toiletId, Pageable pageable);
}
