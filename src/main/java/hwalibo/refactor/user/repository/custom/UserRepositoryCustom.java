package hwalibo.refactor.user.repository.custom;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<Integer> findCalculatedRateByUserId(Long userId);
}
