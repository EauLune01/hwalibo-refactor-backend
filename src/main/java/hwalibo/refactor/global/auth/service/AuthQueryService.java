package hwalibo.refactor.global.auth.service;

import hwalibo.refactor.global.auth.dto.response.ReissueTokenResponse;
import hwalibo.refactor.global.auth.dto.result.ReissueTokenResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true) // 조회 전용이므로 성능 최적화를 위해 readOnly 설정
@RequiredArgsConstructor
public class AuthQueryService {
    public ReissueTokenResponse getReissueResponse(ReissueTokenResult result) {
        return ReissueTokenResponse.from(result);
    }
}
