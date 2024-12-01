package pl.pollub.backend.service.auth;

import pl.pollub.backend.dto.auth.UserDto;
import pl.pollub.backend.model.auth.User;

public interface IUserWriter {
    User saveUser(UserDto userDto);
}
