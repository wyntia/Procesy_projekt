package pl.pollub.backend.controller.movie;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.dto.movie.MovieDto;
import pl.pollub.backend.exception.DatabaseOperationException;
import pl.pollub.backend.exception.InvalidDataException;
import pl.pollub.backend.model.movie.Movie;
import pl.pollub.backend.service.movie.IMovieFilter;
import pl.pollub.backend.service.movie.IMovieReader;
import pl.pollub.backend.service.movie.IMovieWriter;
import pl.pollub.backend.util.filter.GenreFilter;
import pl.pollub.backend.util.filter.YearFilter;


import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final IMovieReader movieReader;
    private final IMovieWriter movieWriter;
    private final IMovieFilter movieFilter;

    public MovieController(IMovieReader movieReader, IMovieWriter movieWriter, IMovieFilter movieFilter) {
        this.movieReader = movieReader;
        this.movieWriter = movieWriter;
        this.movieFilter = movieFilter;
    }

    @PostMapping
    public ResponseEntity<Movie> saveMovie(@Valid @RequestBody MovieDto movieDto) {
        Movie savedMovie = movieWriter.saveMovie(movieDto);
        return ResponseEntity.ok(savedMovie);
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieReader.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieReader.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody MovieDto movieDto) {
        Movie updatedMovie = movieWriter.updateMovie(id, movieDto);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieWriter.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter/genre/{genre}")
    public ResponseEntity<List<Movie>> filterByGenre(@PathVariable String genre) {
        if (genre == null || genre.isEmpty()) {
            throw new InvalidDataException("Genre cannot be null or empty");
        }
        try {
            GenreFilter filter = new GenreFilter(genre);
            Movie[] filteredMovies = movieFilter.filterMovies(filter);
            return ResponseEntity.ok(Arrays.asList(filteredMovies));
        } catch (DatabaseOperationException ex) {
            throw new DatabaseOperationException("Error filtering movies by genre: " + genre, ex);
        }
    }

    @GetMapping("/filter/year/{year}")
    public ResponseEntity<List<Movie>> filterByYear(@PathVariable String year) {
        int parsedYear;
        try {
            parsedYear = Integer.parseInt(year);
        } catch (NumberFormatException ex) {
            throw new InvalidDataException("The year must be a valid integer");
        }

        YearFilter filter = new YearFilter(parsedYear);
        Movie[] filteredMovies = movieFilter.filterMovies(filter);
        return ResponseEntity.ok(Arrays.asList(filteredMovies));
    }

}
