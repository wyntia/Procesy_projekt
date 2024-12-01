package pl.pollub.backend.model.auth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequest{
    @NotBlank(message = "Username is mandatory")
    @NotEmpty
    private String username;
    @NotBlank(message = "Password is mandatory")
    @NotEmpty
    private String password;
}
