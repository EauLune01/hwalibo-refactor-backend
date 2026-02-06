package hwalibo.refactor.review.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.review.domain.ReviewImage;
import hwalibo.refactor.review.domain.ValidationStatus;
import hwalibo.refactor.review.dto.query.PhotoReviewResult;
import hwalibo.refactor.review.dto.query.QPhotoReviewResult;
import hwalibo.refactor.review.repository.custom.ReviewImageRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import java.util.List;
import java.util.Optional;


import static hwalibo.refactor.review.domain.QReview.review;
import static hwalibo.refactor.review.domain.QReviewImage.reviewImage;
import static hwalibo.refactor.user.domain.QUser.user;

@RequiredArgsConstructor
public class ReviewImageRepositoryImpl implements ReviewImageRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PhotoReviewResult> findSliceByToiletId(Long toiletId, Gender gender, Pageable pageable) {

        List<PhotoReviewResult> content = queryFactory
                .select(new QPhotoReviewResult(
                        reviewImage.review.id,
                        reviewImage.id,
                        reviewImage.url
                ))
                .from(reviewImage)
                .join(reviewImage.review, review)
                .where(
                        review.toilet.id.eq(toiletId),
                        review.toilet.gender.eq(gender),
                        reviewImage.status.eq(ValidationStatus.APPROVED)
                )
                .orderBy(reviewImage.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, content);
    }

    @Override
    public Optional<ReviewImage> findPhotoDetail(Long photoId, Gender gender) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(reviewImage)
                        .join(reviewImage.review, review).fetchJoin()
                        .join(review.user, user).fetchJoin()
                        .where(
                                reviewImage.id.eq(photoId),
                                review.toilet.gender.eq(gender),
                                reviewImage.status.eq(ValidationStatus.APPROVED)
                        )
                        .fetchOne()
        );
    }

    /******************** Helper Method ********************/
    private Slice<PhotoReviewResult> checkLastPage(Pageable pageable, List<PhotoReviewResult> content) {
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
