package pl.pollub.backend.util.filter;

import lombok.RequiredArgsConstructor;
import pl.pollub.backend.model.movie.Movie;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GenreFilter extends Filter {
    private final String genre;

    @Override
    public Movie[] filter(Movie[] movies) {
        List<Movie> list = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.getGenre().equalsIgnoreCase(genre)) {
                list.add(movie);
            }
        }
        return list.toArray(new Movie[0]);
    }
}