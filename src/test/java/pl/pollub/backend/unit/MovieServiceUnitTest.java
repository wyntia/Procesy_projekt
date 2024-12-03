package pl.pollub.backend.unit;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataAccessException;
import pl.pollub.backend.dto.movie.MovieDto;
import pl.pollub.backend.exception.DatabaseOperationException;
import pl.pollub.backend.exception.InvalidDataException;
import pl.pollub.backend.exception.MovieNotFoundException;
import pl.pollub.backend.model.movie.Movie;
import pl.pollub.backend.repository.movie.IMovieRepository;
import pl.pollub.backend.service.movie.MovieService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class MovieServiceUnitTest {

    private final IMovieRepository movieRepository = mock(IMovieRepository.class);
    private final MovieService movieService = new MovieService(movieRepository);

    @Test
    void givenValidMovieData_whenSaveMovie_thenReturnsMovie(){

        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Inception");
        movieDto.setGenre("Sci-Fi");
        movieDto.setReleaseDate(LocalDate.of(2010, 7, 16));

        Movie savedMovie = new Movie();
        savedMovie.setId(1L);
        savedMovie.setTitle("Inception");
        savedMovie.setGenre("Sci-Fi");
        savedMovie.setReleaseDate(LocalDate.of(2010, 7, 16));

        when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

        Movie result = movieService.saveMovie(movieDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Inception", result.getTitle());
        assertEquals("Sci-Fi", result.getGenre());
        assertEquals(LocalDate.of(2010, 7, 16), result.getReleaseDate());

        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void givenMovieWithNullTitle_whenSaveMovie_thenThrowsInvalidDataException() {

        MovieDto movieDto = new MovieDto();
        movieDto.setTitle(null);
        movieDto.setGenre("Sci-Fi");
        movieDto.setReleaseDate(LocalDate.of(2010, 7, 16));

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> movieService.saveMovie(movieDto));
        assertEquals("Title cannot be null or empty", exception.getMessage());

        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void givenMovieWithEmptyTitle_whenSaveMovie_thenThrowsInvalidDataException() {

        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("");
        movieDto.setGenre("Sci-Fi");
        movieDto.setReleaseDate(LocalDate.of(2010, 7, 16));

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> movieService.saveMovie(movieDto));
        assertEquals("Title cannot be null or empty", exception.getMessage());

        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void givenRepositoryThrowsDataAccessException_whenSaveMovie_thenThrowsDatabaseOperationException() {

        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Inception");
        movieDto.setGenre("Sci-Fi");
        movieDto.setReleaseDate(LocalDate.of(2010, 7, 16));

        when(movieRepository.save(any(Movie.class))).thenThrow(new DataAccessException("Database error") {});

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class, () -> movieService.saveMovie(movieDto));
        assertEquals("Failed to save movie", exception.getMessage());

        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void givenMovieDto_whenSaveMovie_thenCorrectlyConvertsToEntity() {

        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Inception");
        movieDto.setGenre("Sci-Fi");
        movieDto.setReleaseDate(LocalDate.of(2010, 7, 16));

        Movie savedMovie = new Movie();
        savedMovie.setId(1L);
        savedMovie.setTitle("Inception");
        savedMovie.setGenre("Sci-Fi");
        savedMovie.setReleaseDate(LocalDate.of(2010, 7, 16));

        when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

        movieService.saveMovie(movieDto);

        ArgumentCaptor<Movie> movieCaptor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository).save(movieCaptor.capture());

        Movie capturedMovie = movieCaptor.getValue();
        assertEquals("Inception", capturedMovie.getTitle());
        assertEquals("Sci-Fi", capturedMovie.getGenre());
        assertEquals(LocalDate.of(2010, 7, 16), capturedMovie.getReleaseDate());
    }

    @Test
    void givenExistingMovies_whenGetAllMovies_thenReturnsMovieList() {

        Movie movie1 = new Movie();
        movie1.setId(1L);
        movie1.setTitle("Inception");

        Movie movie2 = new Movie();
        movie2.setId(2L);
        movie2.setTitle("The Matrix");

        List<Movie> movies = Arrays.asList(movie1, movie2);

        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = movieService.getAllMovies();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Inception", result.get(0).getTitle());
        assertEquals("The Matrix", result.get(1).getTitle());

        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void givenNoMovies_whenGetAllMovies_thenReturnsEmptyList() {
        when(movieRepository.findAll()).thenReturn(Collections.emptyList());

        List<Movie> result = movieService.getAllMovies();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void givenRepositoryThrowsDataAccessException_whenGetAllMovies_thenThrowsDatabaseOperationException() {
        when(movieRepository.findAll()).thenThrow(new DataAccessException("Database error") {});

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class, () -> movieService.getAllMovies());
        assertEquals("Failed to retrieve movies", exception.getMessage());

        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void givenExistingMovieId_whenGetMovieById_thenReturnsMovie() {
        Long movieId = 1L;
        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setTitle("Inception");
        movie.setGenre("Sci-Fi");
        movie.setReleaseDate(LocalDate.of(2010, 7, 16));

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        Movie result = movieService.getMovieById(movieId);

        assertNotNull(result);
        assertEquals(movieId, result.getId());
        assertEquals("Inception", result.getTitle());
        assertEquals("Sci-Fi", result.getGenre());
        assertEquals(LocalDate.of(2010, 7, 16), result.getReleaseDate());

        verify(movieRepository, times(1)).findById(movieId);
    }

    @Test
    void givenNonExistentMovieId_whenGetMovieById_thenThrowsMovieNotFoundException() {
        Long movieId = 1L;

        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        MovieNotFoundException exception = assertThrows(MovieNotFoundException.class, () -> movieService.getMovieById(movieId));
        assertEquals("Movie with ID 1 not found", exception.getMessage());

        verify(movieRepository, times(1)).findById(movieId);
    }

    @Test
    void givenRepositoryThrowsDataAccessException_whenGetMovieById_thenThrowsDatabaseOperationException() {

        Long movieId = 1L;

        when(movieRepository.findById(movieId)).thenThrow(new DataAccessException("Database error") {});

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class, () -> movieService.getMovieById(movieId));
        assertEquals("Failed to retrieve movie", exception.getMessage());

        verify(movieRepository, times(1)).findById(movieId);
    }

    @Test
    void givenValidMovieWithSingleFieldUpdate_whenUpdateMovie_thenUpdatesOnlyProvidedField() {

        Long movieId = 1L;
        Movie existingMovie = new Movie();
        existingMovie.setId(movieId);
        existingMovie.setTitle("Original Title");
        existingMovie.setGenre("Original Genre");
        existingMovie.setReleaseDate(LocalDate.of(2000, 1, 1));

        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Updated Title");

        Movie updatedMovie = new Movie();
        updatedMovie.setId(movieId);
        updatedMovie.setTitle("Updated Title");
        updatedMovie.setGenre("Original Genre");
        updatedMovie.setReleaseDate(LocalDate.of(2000, 1, 1));

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);

        Movie result = movieService.updateMovie(movieId, movieDto);

        assertNotNull(result);
        assertEquals(movieId, result.getId());
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Original Genre", result.getGenre());
        assertEquals(LocalDate.of(2000, 1, 1), result.getReleaseDate());

        ArgumentCaptor<Movie> movieCaptor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository, times(1)).save(movieCaptor.capture());

        Movie capturedMovie = movieCaptor.getValue();
        assertEquals("Updated Title", capturedMovie.getTitle());
        assertEquals("Original Genre", capturedMovie.getGenre());
        assertEquals(LocalDate.of(2000, 1, 1), capturedMovie.getReleaseDate());
    }

    @Test
    void givenValidMovieWithMultipleFieldUpdates_whenUpdateMovie_thenUpdatesOnlyProvidedFields() {

        Long movieId = 1L;
        Movie existingMovie = new Movie();
        existingMovie.setId(movieId);
        existingMovie.setTitle("Original Title");
        existingMovie.setGenre("Original Genre");
        existingMovie.setReleaseDate(LocalDate.of(2000, 1, 1));

        MovieDto movieDto = new MovieDto();
        movieDto.setGenre("Updated Genre");
        movieDto.setReleaseDate(LocalDate.of(2020, 5, 20));

        Movie updatedMovie = new Movie();
        updatedMovie.setId(movieId);
        updatedMovie.setTitle("Original Title");
        updatedMovie.setGenre("Updated Genre");
        updatedMovie.setReleaseDate(LocalDate.of(2020, 5, 20));

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);

        Movie result = movieService.updateMovie(movieId, movieDto);

        assertNotNull(result);
        assertEquals(movieId, result.getId());
        assertEquals("Original Title", result.getTitle());
        assertEquals("Updated Genre", result.getGenre());
        assertEquals(LocalDate.of(2020, 5, 20), result.getReleaseDate());

        ArgumentCaptor<Movie> movieCaptor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository, times(1)).save(movieCaptor.capture());

        Movie capturedMovie = movieCaptor.getValue();
        assertEquals("Original Title", capturedMovie.getTitle());
        assertEquals("Updated Genre", capturedMovie.getGenre());
        assertEquals(LocalDate.of(2020, 5, 20), capturedMovie.getReleaseDate());
    }

    @Test
    void givenMovieWithEmptyTitle_whenUpdateMovie_thenThrowsInvalidDataException() {

        Long movieId = 1L;
        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("");
        movieDto.setGenre("Updated Genre");
        movieDto.setReleaseDate(LocalDate.of(2020, 5, 20));

        Movie existingMovie = new Movie();
        existingMovie.setId(movieId);
        existingMovie.setTitle("Original Title");
        existingMovie.setGenre("Original Genre");
        existingMovie.setReleaseDate(LocalDate.of(2000, 1, 1));

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> movieService.updateMovie(movieId, movieDto));
        assertEquals("Title cannot be empty", exception.getMessage());

        verify(movieRepository, times(1)).findById(movieId);
        verify(movieRepository, never()).save(any(Movie.class));
    }


    @Test
    void givenNonExistentMovieId_whenUpdateMovie_thenThrowsMovieNotFoundException() {

        Long movieId = 1L;
        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Updated Title");
        movieDto.setGenre("Updated Genre");
        movieDto.setReleaseDate(LocalDate.of(2020, 1, 1));

        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        MovieNotFoundException exception = assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(movieId, movieDto));
        assertEquals("Movie with ID 1 not found", exception.getMessage());

        verify(movieRepository, times(1)).findById(movieId);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void givenRepositoryThrowsDataAccessException_whenUpdateMovie_thenThrowsDatabaseOperationException() {
        Long movieId = 1L;
        Movie existingMovie = new Movie();
        existingMovie.setId(movieId);
        existingMovie.setTitle("Original Title");
        existingMovie.setGenre("Original Genre");
        existingMovie.setReleaseDate(LocalDate.of(2000, 1, 1));

        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Updated Title");

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.save(any(Movie.class))).thenThrow(new DataAccessException("Database error") {});

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class, () -> movieService.updateMovie(movieId, movieDto));
        assertEquals("Failed to update movie", exception.getMessage());

        verify(movieRepository, times(1)).findById(movieId);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void givenExistingMovieId_whenDeleteMovie_thenMovieIsDeleted() {
        Long movieId = 1L;

        when(movieRepository.existsById(movieId)).thenReturn(true);
        doNothing().when(movieRepository).deleteById(movieId);

        movieService.deleteMovie(movieId);

        verify(movieRepository, times(1)).existsById(movieId);
        verify(movieRepository, times(1)).deleteById(movieId);
    }

    @Test
    void givenNonExistentMovieId_whenDeleteMovie_thenThrowsMovieNotFoundException() {
        Long movieId = 1L;

        when(movieRepository.existsById(movieId)).thenReturn(false);

        MovieNotFoundException exception = assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(movieId));
        assertEquals("Movie with ID 1 not found", exception.getMessage());

        verify(movieRepository, times(1)).existsById(movieId);
        verify(movieRepository, never()).deleteById(anyLong());
    }

    @Test
    void givenRepositoryThrowsDataAccessException_whenDeleteMovie_thenThrowsDatabaseOperationException() {
        Long movieId = 1L;

        when(movieRepository.existsById(movieId)).thenReturn(true);
        doThrow(new DataAccessException("Database error") {}).when(movieRepository).deleteById(movieId);

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class, () -> movieService.deleteMovie(movieId));
        assertEquals("Failed to delete movie", exception.getMessage());

        verify(movieRepository, times(1)).existsById(movieId);
        verify(movieRepository, times(1)).deleteById(movieId);
    }

}