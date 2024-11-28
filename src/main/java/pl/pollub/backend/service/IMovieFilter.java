package pl.pollub.backend.service;

import pl.pollub.backend.util.filter.Filter;
import pl.pollub.backend.model.Movie;

public interface IMovieFilter {
    Movie[] filterMovies(Filter filter);
}