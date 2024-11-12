package pl.pollub.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.dto.MovieDto;
import pl.pollub.backend.model.Movie;
import pl.pollub.backend.service.IMovieReader;
import pl.pollub.backend.service.IMovieWriter;


import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final IMovieReader movieReader;
    private final IMovieWriter movieWriter;

    public MovieController(IMovieReader movieReader, IMovieWriter movieWriter) {
        this.movieReader = movieReader;
        this.movieWriter = movieWriter;
    }

    @PostMapping
    public ResponseEntity<Movie> saveMovie(@RequestBody MovieDto movieDto) {
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
}
