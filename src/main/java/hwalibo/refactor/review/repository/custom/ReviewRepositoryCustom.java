package hwalibo.refactor.review.repository.custom;

import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.dto.query.ReviewSearchCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ReviewRepositoryCustom {
    Slice<Review> findReviewList(Long toiletId, ReviewSearchCondition condition,
                                 List<Long> likedReviewIds, Pageable pageable);
}
