package pl.pollub.backend.repository.auth;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import pl.pollub.backend.model.auth.User;

@Repository
public interface IUserRepository extends CrudRepository<User, Long>
{
    User findByUsername(String username);
}

