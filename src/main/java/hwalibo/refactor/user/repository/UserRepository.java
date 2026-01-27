package hwalibo.refactor.user.repository;

import hwalibo.refactor.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByRefreshToken(String refreshToken);

    boolean existsByName(String name);

    @Query(value = """
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
        """, nativeQuery = true)
    Optional<Integer> findCalculatedRateByUserId(@Param("userId") Long userId);

    @Query(
            value = "SELECT * FROM users WHERE provider = :provider AND provider_id = :providerId LIMIT 1",
            nativeQuery = true
    )
    Optional<User> findUserEvenIfDeleted(@Param("provider") String provider, @Param("providerId") String providerId);
}
