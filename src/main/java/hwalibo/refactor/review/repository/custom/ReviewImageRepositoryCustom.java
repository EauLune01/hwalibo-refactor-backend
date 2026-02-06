package hwalibo.refactor.review.repository.custom;

import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.review.domain.ReviewImage;
import hwalibo.refactor.review.dto.query.PhotoReviewResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface ReviewImageRepositoryCustom {
    Slice<PhotoReviewResult> findSliceByToiletId(Long toiletId, Gender gender, Pageable pageable);
    Optional<ReviewImage> findPhotoDetail(Long photoId, Gender gender);
}
