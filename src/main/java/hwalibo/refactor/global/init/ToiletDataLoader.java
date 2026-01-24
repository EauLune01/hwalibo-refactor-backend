package hwalibo.refactor.global.init;

import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.global.domain.InOut;
import hwalibo.refactor.toilet.domain.Toilet;
import hwalibo.refactor.toilet.respository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("ToiletDataLoader")
@RequiredArgsConstructor
public class ToiletDataLoader implements CommandLineRunner {

    private final ToiletRepository toiletRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (toiletRepository.count() > 0) {
            log.info("✅ Toilet table already initialized. Skipping CSV import.");
            return;
        }

        loadToilets();
    }

    private void loadToilets() {
        List<Toilet> toiletList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        getClass().getResourceAsStream("/data/toilet_preprocessed.csv"),
                        StandardCharsets.UTF_8))) {

            String line;
            br.readLine();
            int skipped = 0;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    skipped++;
                    continue;
                }

                String[] tokens = line.split(",");
                if (tokens.length < 11) {
                    log.warn("⚠️ 유효하지 않은 라인 (컬럼 부족): {}", line);
                    skipped++;
                    continue;
                }

                try {
                    String name = tokens[0].trim();
                    String lineInfo = tokens[1].trim();
                    Gender gender = safeParseEnum(tokens[2], Gender.class);
                    InOut inOut = safeParseEnum(tokens[3], InOut.class);
                    Double latitude = safeParseDouble(tokens[4]);
                    Double longitude = safeParseDouble(tokens[5]);
                    Integer numGate = safeParseInt(tokens[6]);
                    Integer numBigToilet = safeParseInt(tokens[7]);
                    Integer numSmallToilet = safeParseInt(tokens[8]);

                    if (latitude == 0.0 && longitude == 0.0) {
                        skipped++;
                        continue;
                    }

                    Toilet toilet = Toilet.create(
                            name, lineInfo, gender, latitude, longitude,
                            numBigToilet, numSmallToilet, numGate, inOut
                    );

                    toiletList.add(toilet);

                } catch (Exception e) {
                    log.error("❌ 라인 파싱 에러: {}", line, e);
                    skipped++;
                }
            }

            if (!toiletList.isEmpty()) {
                toiletRepository.saveAll(toiletList);
                log.info("✅ {}개의 화장실 데이터가 성공적으로 저장되었습니다.", toiletList.size());
            }
            if (skipped > 0) log.info("⚠️ {}개의 라인이 건너뛰어졌습니다.", skipped);

        } catch (Exception e) {
            log.error("❌ Toilet 데이터 로딩 실패", e);
        }
    }

    // ------------------- Helper methods -------------------

    private Integer safeParseInt(String value) {
        try {
            return (value == null || value.trim().isEmpty()) ? 0 : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) { return 0; }
    }

    private Double safeParseDouble(String value) {
        try {
            return (value == null || value.trim().isEmpty()) ? 0.0 : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) { return 0.0; }
    }

    private <E extends Enum<E>> E safeParseEnum(String value, Class<E> enumClass) {
        try {
            if (value == null || value.trim().isEmpty()) return null;
            return Enum.valueOf(enumClass, value.trim().toUpperCase()); // 대소문자 방어
        } catch (IllegalArgumentException e) { return null; }
    }
}