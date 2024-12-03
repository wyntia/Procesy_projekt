package pl.pollub.backend.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import pl.pollub.backend.config.JwtTokenUtil;
import pl.pollub.backend.controller.auth.JwtAuthenticationController;
import pl.pollub.backend.dto.auth.UserDto;
import pl.pollub.backend.exception.GlobalExceptionHandler;
import pl.pollub.backend.exception.InvalidCredentialsException;
import pl.pollub.backend.model.auth.User;
import pl.pollub.backend.model.auth.JwtRequest;
import pl.pollub.backend.service.auth.JwtUserDetailsService;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JwtAuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class JwtAuthenticationControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUserDetailsService userDetailsService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void givenValidUserCredentials_whenGenerateToken_thenReturnJwtToken() throws Exception {
        JwtRequest request = new JwtRequest("testUser", "testPassword");
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "testUser", "encodedPassword", new ArrayList<>());
        String token = "sample.jwt.token";

        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(token);

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").value(token));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).loadUserByUsername("testUser");
        verify(jwtTokenUtil, times(1)).generateToken(userDetails);
    }

    @Test
    void givenInvalidUserCredentials_whenGenerateToken_thenReturnUnauthorized() throws Exception {
        JwtRequest request = new JwtRequest("testUser", "wrongPassword");

        doThrow(new InvalidCredentialsException("Invalid username or password"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userDetailsService);
        verifyNoInteractions(jwtTokenUtil);
    }

    @Test
    void givenValidUserCredentials_whenRegister_thenReturnUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .username("testUser")
                .password("testPassword")
                .build();

        User savedUser = new User(1L, "testUser", "encodedPassword");

        when(userDetailsService.saveUser(eq(userDto)))
                .thenReturn(savedUser);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userDetailsService, times(1)).saveUser(eq(userDto));
    }

}