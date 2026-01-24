package hwalibo.refactor.review.domain;

import hwalibo.refactor.global.domain.BaseTimeEntity;
import hwalibo.refactor.toilet.domain.Toilet;
import hwalibo.refactor.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toilet_id", nullable = false)
    private Toilet toilet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Double rating;

    @Column(name = "like_count")
    private Integer likeCount;

    private boolean isDisabledAccess;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "review_tags", joinColumns = @JoinColumn(name = "review_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private List<Tag> tags = new ArrayList<>();

    /**
     * Private 생성자
     */
    private Review(User user, Toilet toilet, String content, Double rating, boolean isDisabledAccess, List<Tag> tags) {
        this.user = user;
        this.toilet = toilet;
        this.content = content;
        this.rating = rating;
        this.isDisabledAccess = isDisabledAccess;
        this.tags = (tags != null) ? tags : new ArrayList<>();
        this.likeCount = 0;
    }

    /**
     * 정적 팩토리 메서드
     */
    public static Review create(User user, Toilet toilet, String content, Double rating, boolean isDisabledAccess, List<Tag> tags) {
        user.addReview();
        toilet.updateReviewStats(rating);

        return new Review(user, toilet, content, rating, isDisabledAccess, tags);
    }

    /* ================= 비즈니스 메서드 ================= */

    public void addLike() {
        this.likeCount++;
    }

    public void updateDisabledAccess(boolean isDisabledAccess) {
        this.isDisabledAccess = isDisabledAccess;
    }
}
