package pl.pollub.backend.service;

import pl.pollub.backend.model.Movie;

import java.util.List;

public interface IMovieReader {
    List<Movie> getAllMovies();
    Movie getMovieById(Long id);
}
