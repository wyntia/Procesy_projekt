package pl.pollub.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDto {

    @NotNull
    @NotEmpty
    private String title;

    @NotNull @NotEmpty
    private String genre;

    @NotNull
    private LocalDate releaseDate;
}
