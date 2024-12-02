package pl.pollub.backend.service.movie;

import pl.pollub.backend.model.movie.Movie;

import java.util.List;

public interface IMovieReader {
    List<Movie> getAllMovies();
    Movie getMovieById(Long id);
}
