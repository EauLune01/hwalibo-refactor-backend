package hwalibo.refactor.review.domain;

import hwalibo.refactor.global.domain.BaseTimeEntity;
import hwalibo.refactor.global.exception.image.ImageCountInvalidException;
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

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

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
        toilet.updateReviewStats(0.0, rating, true);
        return new Review(user, toilet, content, rating, isDisabledAccess, tags);
    }

    /* ================= 비즈니스 메서드 ================= */

    public void addLike() {
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        this.likeCount++;
    }

    public void removeLike() {
        if (this.likeCount != null && this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void updateReview(String content, Double rating, boolean isDisabledAccess, List<Tag> tags) {
        this.content = content;
        this.rating = rating;
        this.isDisabledAccess = isDisabledAccess;
        this.tags = (tags != null) ? tags : new ArrayList<>();
    }

    /**
     * 이미지 추가 및 개수 검증
     */
    public void addReviewImage(ReviewImage image) {
        if (this.reviewImages.size() >= 2) {
            throw new ImageCountInvalidException("이미지는 최대 2장까지만 등록 가능합니다.");
        }
        this.reviewImages.add(image);
        reorderImages();
    }

    /**
     * 특정 이미지 삭제
     */
    public void removeReviewImage(Long imageId) {
        this.reviewImages.removeIf(img -> img.getId().equals(imageId));
        reorderImages();
    }

    /**
     * 이미지 순서 재정렬 (내부 로직)
     */
    private void reorderImages() {
        for (int i = 0; i < reviewImages.size(); i++) {
            reviewImages.get(i).updateSortOrder(i + 1);
        }
    }
}
