package hwalibo.refactor.reviewLike.domain;

import hwalibo.refactor.global.domain.BaseTimeEntity;
import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "review_like",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_review_like_user_review",
                        columnNames = {"user_id", "review_id"}
                )
        }
)
public class ReviewLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    /**
     * Private 생성자
     */
    private ReviewLike(User user, Review review) {
        this.user = user;
        this.review = review;
    }

    /**
     * 정적 팩토리 메서드: 생성 시점의 로직 제어
     */
    public static ReviewLike create(User user, Review review) {
        review.addLike();
        return new ReviewLike(user, review);
    }
}