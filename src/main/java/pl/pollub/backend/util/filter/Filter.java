package pl.pollub.backend.util.filter;

import pl.pollub.backend.model.movie.Movie;

public abstract class Filter {
    public abstract Movie[] filter(Movie[] movies);
}