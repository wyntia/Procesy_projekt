package pl.pollub.backend.service;

import pl.pollub.backend.dto.MovieDto;
import pl.pollub.backend.model.Movie;

public interface IMovieService {

    Movie saveMovie(MovieDto movieDto);
    Movie getMovieById(Long id);
    Movie updateMovie(Long id, MovieDto movieDto);
    void deleteMovie(Long id);

}
