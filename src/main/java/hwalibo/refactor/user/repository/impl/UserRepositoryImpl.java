package hwalibo.refactor.user.repository.impl;

import hwalibo.refactor.user.repository.custom.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final EntityManager em;

    @Override
    public Optional<Integer> findCalculatedRateByUserId(Long userId) {
        String sql = """
            SELECT calculated_rank.rate
            FROM (
                SELECT
                    u.user_id as id,
                    CEIL(
                        PERCENT_RANK() OVER (ORDER BY u.num_review DESC) * 100
                    ) AS rate
                FROM
                    users u
                WHERE
                    u.status = 'ACTIVE') AS calculated_rank
            WHERE
                calculated_rank.id = :userId
            """;

        try {
            Object result = em.createNativeQuery(sql)
                    .setParameter("userId", userId)
                    .getSingleResult();

            return Optional.of(((Number) result).intValue());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
