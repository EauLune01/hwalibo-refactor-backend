package hwalibo.refactor.review.domain;

import hwalibo.refactor.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_images")
public class ReviewImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationStatus status;

    @Column(nullable = false, length = 1024)
    private String url;

    @Column(nullable = false)
    private Integer sortOrder;

    /**
     * Private 생성자
     */
    private ReviewImage(Review review, String url, Integer sortOrder) {
        this.review = review;
        this.url = url;
        this.sortOrder = sortOrder;
        this.status = ValidationStatus.PENDING;
    }

    /**
     * 정적 팩토리 메서드
     */
    public static ReviewImage create(Review review, String url, Integer sortOrder) {
        return new ReviewImage(review, url, sortOrder);
    }

    /* ================= 비즈니스 메서드 ================= */

    public void updateUrl(String newUrl) {
        if (newUrl != null && !newUrl.isBlank()) {
            this.url = newUrl;
        }
    }

    public void approve() {
        this.status = ValidationStatus.APPROVED;
    }

    public void reject() {
        this.status = ValidationStatus.REJECTED;
    }
}
