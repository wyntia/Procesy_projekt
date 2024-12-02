package pl.pollub.backend.service.movie;

import pl.pollub.backend.dto.movie.MovieDto;
import pl.pollub.backend.model.movie.Movie;

public interface IMovieWriter {

    Movie saveMovie(MovieDto movieDto);
    Movie updateMovie(Long id, MovieDto movieDto);
    void deleteMovie(Long id);

}
