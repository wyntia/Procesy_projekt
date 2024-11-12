package pl.pollub.backend.model;
import lombok.Getter;
import java.io.Serializable;

@Getter
public class JwtResponse implements Serializable {
    private final String jwtToken;
    public JwtResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
