package hwalibo.refactor.toilet.respository;

import hwalibo.refactor.toilet.domain.Toilet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToiletRepository extends JpaRepository<Toilet, Long> {
}
