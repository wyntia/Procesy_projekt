package pl.pollub.backend.service.movie;

import pl.pollub.backend.util.filter.Filter;
import pl.pollub.backend.model.movie.Movie;

public interface IMovieFilter {
    Movie[] filterMovies(Filter filter);
}