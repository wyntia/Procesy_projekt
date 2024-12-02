package pl.pollub.backend.model.auth;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponse{
    private final String jwtToken;
}
