package pl.pollub.backend.repository;
import org.springframework.stereotype.Repository;
import pl.pollub.backend.model.UserDao;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface UserRepository extends CrudRepository<UserDao, Integer>
{
    UserDao findByUsername(String username);
}

