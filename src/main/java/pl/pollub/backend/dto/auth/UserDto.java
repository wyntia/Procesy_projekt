package pl.pollub.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class UserDto {
    @NotBlank(message = "Username is mandatory")
    @NotEmpty
    private String username;
    @NotBlank(message = "Password is mandatory")
    @NotEmpty
    private String password;
}