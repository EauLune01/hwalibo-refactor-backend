package hwalibo.refactor.review.repository;

import hwalibo.refactor.review.domain.ReviewImage;
import hwalibo.refactor.review.domain.ValidationStatus;
import hwalibo.refactor.review.repository.custom.ReviewImageRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long>, ReviewImageRepositoryCustom {
    List<ReviewImage> findAllByStatusAndUpdatedAtBefore(ValidationStatus status, LocalDateTime dateTime);
}
