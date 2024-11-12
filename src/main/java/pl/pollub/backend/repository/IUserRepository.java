package pl.pollub.backend.repository;
import org.springframework.stereotype.Repository;
import pl.pollub.backend.model.User;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface IUserRepository extends CrudRepository<User, Long>
{
    User findByUsername(String username);
}

