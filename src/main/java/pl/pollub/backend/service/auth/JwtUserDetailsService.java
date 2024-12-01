package pl.pollub.backend.service.auth;
import pl.pollub.backend.exception.UserSaveException;
import pl.pollub.backend.dto.auth.UserDto;
import pl.pollub.backend.model.auth.User;
import pl.pollub.backend.repository.auth.IUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService, IUserWriter, IUserReader {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtUserDetailsService(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    @Override
    public User saveUser(UserDto userDto) {
        String username = userDto.getUsername().trim();
        if (username.isEmpty()) {
            throw new UserSaveException("Username cannot be empty or consist solely of whitespace characters");
        }

        User existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser != null) {
            throw new UserSaveException("User with username " + userDto.getUsername() + " already exists");
        }

        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(newUser);
    }
}
