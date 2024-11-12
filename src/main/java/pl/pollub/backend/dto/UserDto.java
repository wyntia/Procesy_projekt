package pl.pollub.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    @NotBlank(message = "Username is mandatory")
    @NotEmpty
    private String username;
    @NotBlank(message = "Password is mandatory")
    @NotEmpty
    private String password;
}