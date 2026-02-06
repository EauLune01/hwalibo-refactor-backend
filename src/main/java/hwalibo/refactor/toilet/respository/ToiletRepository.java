package hwalibo.refactor.toilet.respository;

import hwalibo.refactor.toilet.domain.Toilet;
import hwalibo.refactor.toilet.respository.custom.ToiletRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToiletRepository extends JpaRepository<Toilet, Long>, ToiletRepositoryCustom {
}
