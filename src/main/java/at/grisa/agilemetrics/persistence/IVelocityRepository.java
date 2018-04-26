package at.grisa.agilemetrics.persistence;

import at.grisa.agilemetrics.persistence.entity.Velocity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IVelocityRepository extends CrudRepository<Velocity, Long> {
    List<Velocity> findByTeam(String team);
}
