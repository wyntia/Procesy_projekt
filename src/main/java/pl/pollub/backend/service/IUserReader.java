package pl.pollub.backend.service;

import pl.pollub.backend.model.User;

public interface IUserReader {
    User getUserByUsername(String username);
}
