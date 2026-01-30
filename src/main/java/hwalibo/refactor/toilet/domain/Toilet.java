package hwalibo.refactor.toilet.domain;

import hwalibo.refactor.global.domain.BaseTimeEntity;
import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.global.domain.InOut;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "toilets")
public class Toilet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toilet_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String line;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double star;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private Integer numBigToilet;
    private Integer numSmallToilet;

    private Integer numGate;

    @Enumerated(EnumType.STRING)
    private InOut inOut;

    @Column(name = "num_review")
    private Integer numReview;

    /**
     * Private 생성자
     */
    private Toilet(String name, String line, Gender gender, Double latitude, Double longitude,
                   Integer numBigToilet, Integer numSmallToilet, Integer numGate, InOut inOut) {
        this.name = name;
        this.line = line;
        this.gender = gender;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numBigToilet = numBigToilet;
        this.numSmallToilet = numSmallToilet;
        this.numGate = numGate;
        this.inOut = inOut;
        this.star = 0.0;     // 초기값 설정
        this.numReview = 0;  // 초기값 설정
    }

    /**
     * 정적 팩토리 메서드
     */
    public static Toilet create(String name, String line, Gender gender, Double latitude, Double longitude,
                                Integer numBigToilet, Integer numSmallToilet, Integer numGate, InOut inOut) {
        return new Toilet(name, line, gender, latitude, longitude, numBigToilet, numSmallToilet, numGate, inOut);
    }

    /* ================= 비즈니스 로직 ================= */

    public void updateReviewStats(double oldStar, double newStar, boolean isNewReview) {
        int currentNum = (this.numReview != null ? this.numReview : 0);
        double currentStar = (this.star != null ? this.star : 0.0);
        double currentTotalStars = currentStar * currentNum;
        if (isNewReview) {this.numReview = currentNum + 1;}
        this.star = (currentTotalStars - oldStar + newStar) / this.numReview;
    }

    public void removeReviewStats(double removedReviewStar) {
        if (this.numReview == null || this.numReview <= 1) {
            this.star = 0.0;
            this.numReview = 0;
            return;
        }

        double totalStars = this.star * this.numReview;
        this.numReview -= 1;
        this.star = (totalStars - removedReviewStar) / this.numReview;
    }
}
