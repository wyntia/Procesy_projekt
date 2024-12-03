package pl.pollub.backend.unit;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pollub.backend.dto.auth.UserDto;
import pl.pollub.backend.exception.UserSaveException;
import pl.pollub.backend.model.auth.User;
import pl.pollub.backend.repository.auth.IUserRepository;
import pl.pollub.backend.service.auth.JwtUserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUserDetailsServiceUnitTest {

    private final IUserRepository userRepository = mock(IUserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtUserDetailsService userDetailsService = new JwtUserDetailsService(userRepository, passwordEncoder);

    @Test
    void givenExistingUser_whenLoadByUsername_thenReturnUserDetails() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");

        when(userRepository.findByUsername(username)).thenReturn(user);

        var userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void givenNonexistentUser_whenLoadByUsername_thenThrowUsernameNotFoundException() {
        String username = "nonexistent";

        when(userRepository.findByUsername(username)).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username));

        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void givenValidUserDto_whenSaveUser_thenSaveAndReturnUser() {
        UserDto userDto = new UserDto();
        userDto.setUsername("newuser");
        userDto.setPassword("password");

        User savedUser = new User();
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(null);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userDetailsService.saveUser(userDto);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());

        verify(userRepository, times(1)).findByUsername("newuser");
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenExistingUsername_whenSaveUser_thenThrowUserSaveException() {
        UserDto userDto = new UserDto();
        userDto.setUsername("existinguser");
        userDto.setPassword("password");

        User existingUser = new User();
        existingUser.setUsername("existinguser");

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(existingUser);

        UserSaveException exception = assertThrows(UserSaveException.class, () -> userDetailsService.saveUser(userDto));

        assertEquals("User with username existinguser already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("existinguser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenValidUserDto_whenSaveUser_thenConvertDtoToEntity() {
        UserDto userDto = new UserDto();
        userDto.setUsername("newuser");
        userDto.setPassword("password");

        User savedUser = new User();
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(null);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        userDetailsService.saveUser(userDto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("newuser", capturedUser.getUsername());
        assertEquals("encodedPassword", capturedUser.getPassword());
    }

    @Test
    void givenExistingUser_whenGetByUsername_thenReturnUser() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(user);

        User result = userDetailsService.getUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void givenNonexistentUser_whenGetByUsername_thenThrowUsernameNotFoundException() {
        String username = "nonexistent";

        when(userRepository.findByUsername(username)).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.getUserByUsername(username));

        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

}