package pl.pollub.backend.service;

import pl.pollub.backend.dto.UserDto;
import pl.pollub.backend.model.User;

public interface IUserWriter {
    User saveUser(UserDto userDto);
}
