package hwalibo.refactor.user.repository;

import hwalibo.refactor.user.domain.User;
import hwalibo.refactor.user.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByRefreshToken(String refreshToken);

    boolean existsByName(String name);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
