package pl.pollub.backend.util.filter;

import lombok.RequiredArgsConstructor;
import pl.pollub.backend.model.Movie;
import java.util.Arrays;

@RequiredArgsConstructor
public class YearFilter extends Filter {

    public final int year;

    @Override
    public Movie[] filter(Movie[] movies) {
        return Arrays.stream(movies)
                .filter(movie -> movie.getReleaseDate().getYear() == year)
                .toArray(Movie[]::new);
    }
}