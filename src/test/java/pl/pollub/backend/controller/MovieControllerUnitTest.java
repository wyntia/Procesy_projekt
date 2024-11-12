package pl.pollub.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.pollub.backend.dto.MovieDto;
import pl.pollub.backend.exception.GlobalExceptionHandler;
import pl.pollub.backend.exception.InvalidDataException;
import pl.pollub.backend.exception.MovieNotFoundException;
import pl.pollub.backend.model.Movie;
import pl.pollub.backend.service.IMovieReader;
import pl.pollub.backend.service.IMovieWriter;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class MovieControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IMovieWriter movieWriter;

    @MockBean
    private IMovieReader movieReader;

    @Test
    void saveMovie_withValidData_savesAndReturnsMovie() throws Exception {
        MovieDto movieDto = MovieDto.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .build();

        Movie savedMovie = new Movie(1L, "Inception", "Sci-Fi", LocalDate.of(2010, 7, 16));
        when(movieWriter.saveMovie(movieDto)).thenReturn(savedMovie);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"))
                .andExpect(jsonPath("$.releaseDate").value("2010-07-16"));

        verify(movieWriter, times(1)).saveMovie(movieDto);
    }

    @Test
    void saveMovie_withInvalidData_returnsBadRequest() throws Exception {

        MovieDto movieDto = MovieDto.builder()
                .title("")
                .genre("Sci-Fi")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .build();

        when(movieWriter.saveMovie(any(MovieDto.class)))
                .thenThrow(new InvalidDataException("Title cannot be null or empty"));

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(movieWriter, times(1)).saveMovie(any(MovieDto.class));
    }

    @Test
    void getAllMovies_returnsListOfMovies() throws Exception {

        Movie movie1 = new Movie(1L,"Inception", "Sci-Fi", LocalDate.of(2010, 7, 16));
        Movie movie2 = new Movie(2L,"Interstellar", "Sci-Fi", LocalDate.of(2014, 11, 7));
        when(movieReader.getAllMovies()).thenReturn(List.of(movie1, movie2));

        String responseContent = mockMvc.perform(get("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Movie> movies = objectMapper.readValue(responseContent, new TypeReference<List<Movie>>() {});
        assertThat(movies).hasSize(2);
        assertThat(movies).extracting(Movie::getTitle).containsExactlyInAnyOrder("Inception", "Interstellar");
    }

    @Test
    void getMovieById_existingId_returnsMovie() throws Exception {

        Long movieId = 1L;
        Movie movie = new Movie(movieId,"Inception", "Sci-Fi", LocalDate.of(2010, 7, 16));

        when(movieReader.getMovieById(movieId)).thenReturn(movie);

        mockMvc.perform(get("/api/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(movieId))
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"))
                .andExpect(jsonPath("$.releaseDate").value("2010-07-16"));

        verify(movieReader, times(1)).getMovieById(movieId);
    }

    @Test
    void getMovieById_nonExistingId_returnsNotFound() throws Exception {

        Long movieId = 999L;
        when(movieReader.getMovieById(movieId)).thenThrow(new MovieNotFoundException(movieId));

        mockMvc.perform(get("/api/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Movie with ID " + movieId + " not found"));

        verify(movieReader, times(1)).getMovieById(movieId);
    }

    @Test
    void updateMovie_existingId_returnsUpdatedMovie() throws Exception {

        Long movieId = 1L;
        MovieDto movieDto = new MovieDto("Updated Title", "Drama", LocalDate.of(2020, 5, 15));
        Movie updatedMovie = new Movie(movieId,"Updated Title", "Drama", LocalDate.of(2020, 5, 15));
        updatedMovie.setId(movieId);

        when(movieWriter.updateMovie(eq(movieId), any(MovieDto.class))).thenReturn(updatedMovie);

        mockMvc.perform(put("/api/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(movieId))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.genre").value("Drama"))
                .andExpect(jsonPath("$.releaseDate").value("2020-05-15"));

        verify(movieWriter, times(1)).updateMovie(eq(movieId), any(MovieDto.class));
    }

    @Test
    void updateMovie_nonExistingId_returnsNotFound() throws Exception {

        Long movieId = 999L;
        MovieDto movieDto = new MovieDto("Updated Title", "Drama", LocalDate.of(2020, 5, 15));

        when(movieWriter.updateMovie(eq(movieId), any(MovieDto.class))).thenThrow(new MovieNotFoundException(movieId));

        mockMvc.perform(put("/api/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Movie with ID " + movieId + " not found"));

        verify(movieWriter, times(1)).updateMovie(eq(movieId), any(MovieDto.class));
    }

    @Test
    void deleteMovie_existingId_returnsNoContent() throws Exception {

        Long movieId = 1L;

        mockMvc.perform(delete("/api/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(movieWriter, times(1)).deleteMovie(movieId);
    }

    @Test
    void deleteMovie_nonExistingId_returnsNotFound() throws Exception {

        Long movieId = 999L;

        doThrow(new MovieNotFoundException(movieId)).when(movieWriter).deleteMovie(movieId);

        mockMvc.perform(delete("/api/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Movie with ID " + movieId + " not found"));

        verify(movieWriter, times(1)).deleteMovie(movieId);
    }
}