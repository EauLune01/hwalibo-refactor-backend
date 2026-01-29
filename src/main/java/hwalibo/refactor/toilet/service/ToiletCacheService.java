package hwalibo.refactor.toilet.service;

import hwalibo.refactor.global.exception.toilet.ToiletNotFoundException;
import hwalibo.refactor.toilet.domain.Toilet;
import hwalibo.refactor.toilet.dto.result.ToiletDetailResult;
import hwalibo.refactor.toilet.respository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ToiletCacheService {

    private final ToiletRepository toiletRepository;

    @Cacheable(value = "toiletDetail", key = "#toiletId")
    public ToiletDetailResult getToiletDetail(Long toiletId) {
        log.info("Cache Miss! DB 조회 발생 - toiletId: {}", toiletId);

        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(()->new ToiletNotFoundException("해당 화장실을 조회할 수 없습니다."));

        return ToiletDetailResult.from(toilet);
    }


    @CacheEvict(value = "toiletDetail", key = "#toiletId")
    public void evictToiletCache(Long toiletId) {
        log.info("Toilet Cache Evicted! - toiletId: {}", toiletId);
    }
}
