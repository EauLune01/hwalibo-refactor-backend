package hwalibo.refactor.review.repository;

import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.review.domain.ReviewImage;
import hwalibo.refactor.review.domain.ValidationStatus;
import hwalibo.refactor.review.dto.result.PhotoDetailResult;
import hwalibo.refactor.review.dto.result.PhotoReviewResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewImageRepository extends JpaRepository<ReviewImage,Long> {

    @Query("SELECT ri.review.id as reviewId, " +
            "ri.id as photoId, " +
            "ri.url as photoUrl " +
            "FROM ReviewImage ri " +
            "WHERE ri.review.toilet.id = :toiletId " +
            "AND ri.review.toilet.gender = :gender " +
            "ORDER BY ri.id DESC")
    Slice<PhotoReviewResult> findSliceByToiletId(
            @Param("toiletId") Long toiletId,
            @Param("gender") Gender gender,
            Pageable pageable);

    @Query("SELECT ri FROM ReviewImage ri " +
            "JOIN FETCH ri.review r " +
            "JOIN FETCH r.user u " +
            "WHERE ri.id = :photoId " +
            "AND r.toilet.gender = :gender")
    Optional<ReviewImage> findPhotoDetail(@Param("photoId") Long photoId, @Param("gender") Gender gender);

    List<ReviewImage> findAllByStatusAndUpdatedAtBefore(ValidationStatus status, LocalDateTime dateTime);
}
