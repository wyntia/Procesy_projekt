package pl.pollub.backend.service.auth;

import pl.pollub.backend.model.auth.User;

public interface IUserReader {
    User getUserByUsername(String username);
}
