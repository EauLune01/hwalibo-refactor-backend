package hwalibo.refactor.global.init;

import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.domain.Tag;
import hwalibo.refactor.review.repository.ReviewRepository;
import hwalibo.refactor.toilet.domain.Toilet;
import hwalibo.refactor.toilet.respository.ToiletRepository;
import hwalibo.refactor.user.domain.User;
import hwalibo.refactor.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component("ReviewDataLoader")
@RequiredArgsConstructor
@DependsOn("ToiletDataLoader")
public class ReviewDataLoader implements CommandLineRunner {

    private final ReviewRepository reviewRepository;
    private final ToiletRepository toiletRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (reviewRepository.count() > 0) {
            log.info("✅ Review table already initialized. Skipping dummy review creation.");
            return;
        }

        User tester = User.create(
                "naver",
                "12345678",
                "김도영",
                "https://example.com/profile.png",
                Gender.M
        );
        userRepository.save(tester);

        toiletRepository.findById(1L).ifPresentOrElse(toilet -> {
            saveDummyReviews(tester, toilet);
            log.info("✅ Toilet ID 1에 대해 태그가 포함된 10개의 리뷰 데이터가 로드되었습니다.");
        }, () -> {
            log.error("❌ Toilet ID 1을 찾을 수 없습니다. 데이터 로딩 순서를 확인하세요.");
        });
    }

    private void saveDummyReviews(User user, Toilet toilet) {
        List<String> contents = Arrays.asList(
                "역이랑 가까워서 정말 편리해요! 청소 상태도 아주 좋습니다.",
                "휴지가 가끔 없을 때가 있는데 오늘은 넉넉하네요.",
                "세면대가 넓고 깨끗해서 사용하기 편합니다.",
                "여기는 항상 관리가 잘 되는 것 같아요. 믿고 옵니다.",
                "냄새도 안 나고 채광도 좋아서 쾌적하네요.",
                "사람이 좀 많긴 하지만 칸수가 넉넉해서 금방 줄어들어요.",
                "손 세정제 향기가 너무 좋아요 ㅋㅋㅋ 만족!",
                "입구 찾기가 조금 헷갈렸는데 내부는 정말 넓어요.",
                "화장실 거울이 너무 더러워요.",
                "장애인 화장실 버튼도 잘 작동하고 아주 만족스러운 곳입니다."
        );

        List<Double> ratings = Arrays.asList(5.0, 4.0, 4.5, 5.0, 4.0, 3.5, 5.0, 4.0, 3.5, 5.0);

        List<List<Tag>> tagLists = Arrays.asList(
                List.of(Tag.TOILET_CLEAN, Tag.BRIGHT_LIGHTING),
                List.of(Tag.ENOUGH_HANDSOAP),
                List.of(Tag.SINK_CLEAN),
                List.of(Tag.TOILET_CLEAN, Tag.GOOD_VENTILATION),
                List.of(Tag.GOOD_VENTILATION, Tag.BRIGHT_LIGHTING),
                List.of(Tag.TOILET_CLEAN),
                List.of(Tag.ENOUGH_HANDSOAP, Tag.SINK_CLEAN),
                List.of(Tag.BRIGHT_LIGHTING),
                List.of(Tag.DIRTY_MIRROR),
                List.of(Tag.TOILET_CLEAN, Tag.ENOUGH_HANDSOAP)
        );

        for (int i = 0; i < 10; i++) {
            Review review = Review.create(
                    user,
                    toilet,
                    contents.get(i),
                    ratings.get(i),
                    i == 9,
                    tagLists.get(i)
            );
            reviewRepository.save(review);
        }
    }
}
