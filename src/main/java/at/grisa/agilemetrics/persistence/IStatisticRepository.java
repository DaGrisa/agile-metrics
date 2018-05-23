package at.grisa.agilemetrics.persistence;

import at.grisa.agilemetrics.persistence.entity.Statistic;
import org.springframework.data.repository.CrudRepository;

public interface IStatisticRepository extends CrudRepository<Statistic, Long> {
    Statistic findFirstByOrderByLastRunDesc();
}
