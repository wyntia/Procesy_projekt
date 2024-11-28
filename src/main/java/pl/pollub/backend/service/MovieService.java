package pl.pollub.backend.service;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.pollub.backend.dto.MovieDto;
import pl.pollub.backend.exception.DatabaseOperationException;
import pl.pollub.backend.exception.InvalidDataException;
import pl.pollub.backend.exception.MovieNotFoundException;
import pl.pollub.backend.model.Movie;
import pl.pollub.backend.repository.IMovieRepository;
import pl.pollub.backend.util.filter.Filter;

import java.util.List;

@Service
public class MovieService implements IMovieReader, IMovieWriter, IMovieFilter {

    private final IMovieRepository movieRepository;

    public MovieService(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Movie saveMovie(MovieDto movieDto) {
        validateMovieData(movieDto);
        Movie movie = convertToEntity(movieDto);
        try {
            return movieRepository.save(movie);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Failed to save movie", ex);
        }
    }

    @Override
    public List<Movie> getAllMovies() {
        try {
            return movieRepository.findAll();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Failed to retrieve movies", ex);
        }
    }

    @Override
    public Movie getMovieById(Long id) {
        try {
            return movieRepository.findById(id)
                    .orElseThrow(() -> new MovieNotFoundException(id));
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Failed to retrieve movie", ex);
        }
    }

    @Override
    public Movie updateMovie(Long id, MovieDto movieDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));
        updateMovieFields(movie, movieDto);
        try {
            return movieRepository.save(movie);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Failed to update movie", ex);
        }
    }

    @Override
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException(id);
        }
        try {
            movieRepository.deleteById(id);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Failed to delete movie", ex);
        }
    }

    private Movie convertToEntity(MovieDto movieDto) {
        return Movie.builder()
                .title(movieDto.getTitle())
                .genre(movieDto.getGenre())
                .releaseDate(movieDto.getReleaseDate())
                .build();
    }

    private void updateMovieFields(Movie movie, MovieDto movieDto) {
        if (movieDto.getTitle() != null) {
            if(movieDto.getTitle().isEmpty()){
                throw new InvalidDataException("Title cannot be empty");
            }
            movie.setTitle(movieDto.getTitle());
        }
        if (movieDto.getGenre() != null) {
            if(movieDto.getGenre().isEmpty()){
                throw new InvalidDataException("Genre cannot be empty");
            }
            movie.setGenre(movieDto.getGenre());
        }
        if (movieDto.getReleaseDate() != null) {
            movie.setReleaseDate(movieDto.getReleaseDate());
        }
    }


    private void validateMovieData(MovieDto movieDto) {
        if (movieDto.getTitle() == null || movieDto.getTitle().isEmpty()) {
            throw new InvalidDataException("Title cannot be null or empty");
        }
        if (movieDto.getGenre() == null || movieDto.getGenre().isEmpty()) {
            throw new InvalidDataException("Genre cannot be null or empty");
        }
        if (movieDto.getReleaseDate() == null) {
            throw new InvalidDataException("Release date cannot be null");
        }
    }

    @Override
    public Movie[] filterMovies(Filter filter) {
        List<Movie> movies = getAllMovies();
        try {
            return filter.filter(movies.toArray(new Movie[0]));
        } catch (Exception ex) {
            throw new DatabaseOperationException("Error occurred during filtering", ex);
        }
    }

}
