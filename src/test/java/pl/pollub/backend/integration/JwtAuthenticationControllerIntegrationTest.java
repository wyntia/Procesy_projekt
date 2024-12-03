package pl.pollub.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.pollub.backend.dto.auth.UserDto;
import pl.pollub.backend.model.auth.User;
import pl.pollub.backend.repository.auth.IUserRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtAuthenticationControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto validUserDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        validUserDto = UserDto.builder()
                .username("testuser")
                .password("password123")
                .build();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode("password");

        User user = User.builder()
                .username("existinguser")
                .password(hashedPassword)
                .build();
        userRepository.save(user);
    }

    @Test
    void givenValidUserData_whenRegisterUser_thenReturnCreatedUser() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(validUserDto.getUsername())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void givenValidUserCredentials_whenAuthenticateUser_thenReturnJwtToken() throws Exception {
        UserDto loginDto = UserDto.builder()
                .username("existinguser")
                .password("password")
                .build();

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken", not(emptyOrNullString())));
    }

    @Test
    void givenInvalidUserCredentials_whenAuthenticateUser_thenReturnUnauthorized() throws Exception {
        UserDto invalidLoginDto = UserDto.builder()
                .username("existinguser")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenNonExistingUserCredentials_whenAuthenticateUser_thenReturnUnauthorized() throws Exception {
        UserDto nonExistingUserDto = UserDto.builder()
                .username("nonexistinguser")
                .password("password123")
                .build();

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistingUserDto)))
                .andExpect(status().isUnauthorized());
    }

}