package hwalibo.refactor.toilet.service;

import hwalibo.refactor.global.exception.toilet.ToiletNotFoundException;
import hwalibo.refactor.toilet.domain.Toilet;
import hwalibo.refactor.toilet.dto.result.ToiletDetailResult;
import hwalibo.refactor.toilet.respository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ToiletQueryService {

    private final ToiletCacheService toiletCacheService;

    public ToiletDetailResult getToiletDetail(Long toiletId) {
        return toiletCacheService.getToiletDetail(toiletId);
    }
}
