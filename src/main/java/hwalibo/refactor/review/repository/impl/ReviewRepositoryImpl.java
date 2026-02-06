package hwalibo.refactor.review.repository.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.domain.SortType;
import hwalibo.refactor.review.domain.Tag;
import hwalibo.refactor.review.dto.query.ReviewSearchCondition;
import hwalibo.refactor.review.repository.custom.ReviewRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import static hwalibo.refactor.review.domain.QReview.review;
import static hwalibo.refactor.toilet.domain.QToilet.toilet;
import static hwalibo.refactor.user.domain.QUser.user;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Review> findReviewList(Long toiletId, ReviewSearchCondition condition, List<Long> likedReviewIds, Pageable pageable) {
        List<Review> content = queryFactory
                .selectFrom(review)
                .join(review.toilet, toilet).fetchJoin()
                .join(review.user, user).fetchJoin()
                .where(allFilters(toiletId, condition, likedReviewIds))
                .orderBy(getSortOrder(condition.getSortType()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, content);
    }

    /******************** Helper Method ********************/

    private BooleanExpression toiletIdEq(Long toiletId) {
        return toiletId != null ? review.toilet.id.eq(toiletId) : null;
    }

    private BooleanExpression genderEq(Gender gender) {
        return gender != null ? review.toilet.gender.eq(gender) : null;
    }

    private BooleanExpression hasPhotos(Boolean hasPhotos) {
        if (hasPhotos == null || !hasPhotos) return null;
        return review.reviewImages.isNotEmpty();
    }

    private BooleanExpression tagsContains(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) return null;
        return review.tags.any().in(tags);
    }

    private BooleanExpression onlyLikedFilter(Boolean onlyLiked, List<Long> likedReviewIds) {
        if (onlyLiked == null || !onlyLiked) {
            return null;
        }

        if (likedReviewIds == null || likedReviewIds.isEmpty()) {
            return Expressions.asBoolean(false).isTrue();
        }
        return review.id.in(likedReviewIds);
    }

    private BooleanExpression[] allFilters(Long toiletId, ReviewSearchCondition condition,
                                           List<Long> likedReviewIds) {
        return new BooleanExpression[] {
                toiletIdEq(toiletId),
                genderEq(condition.getGender()),
                hasPhotos(condition.getHasPhotos()),
                tagsContains(condition.getTags()),
                onlyLikedFilter(condition.getOnlyLiked(), likedReviewIds)
        };
    }

    private OrderSpecifier<?> getSortOrder(SortType sortType) {
        if (sortType == null) return review.createdAt.desc();

        return switch (sortType) {
            case RATING -> review.rating.desc();
            case HANDICAPPED -> review.isDisabledAccess.desc();
            case LATEST -> review.createdAt.desc();
        };
    }

    private Slice<Review> checkLastPage(Pageable pageable, List<Review> content) {
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
