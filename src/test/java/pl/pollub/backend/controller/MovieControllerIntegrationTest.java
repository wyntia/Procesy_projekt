package pl.pollub.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.pollub.backend.dto.MovieDto;
import pl.pollub.backend.model.Movie;
import pl.pollub.backend.repository.IMovieRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IMovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieDto validMovieDto;
    private Movie savedMovie;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();

        validMovieDto = new MovieDto();
        validMovieDto.setTitle("Test Movie");
        validMovieDto.setGenre("Action");
        validMovieDto.setReleaseDate(LocalDate.of(2024, 1, 1));

        savedMovie = movieRepository.save(Movie.builder()
                .title("Existing Movie")
                .genre("Drama")
                .releaseDate(LocalDate.of(2023, 12, 1))
                .build());
    }

    @Test
    void saveMovie_WithValidData_ShouldReturnCreatedMovie() throws Exception {
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMovieDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(validMovieDto.getTitle())))
                .andExpect(jsonPath("$.genre", is(validMovieDto.getGenre())))
                .andExpect(jsonPath("$.releaseDate", is(validMovieDto.getReleaseDate().toString())));
    }

    @Test
    void saveMovie_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        MovieDto invalidMovieDto = new MovieDto();
        // Missing required fields

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovieDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllMovies_ShouldReturnListOfMovies() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title", is(savedMovie.getTitle())))
                .andExpect(jsonPath("$[0].genre", is(savedMovie.getGenre())))
                .andExpect(jsonPath("$[0].releaseDate", is(savedMovie.getReleaseDate().toString())));
    }

    @Test
    void getMovieById_WithExistingId_ShouldReturnMovie() throws Exception {
        mockMvc.perform(get("/api/movies/{id}", savedMovie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(savedMovie.getTitle())))
                .andExpect(jsonPath("$.genre", is(savedMovie.getGenre())))
                .andExpect(jsonPath("$.releaseDate", is(savedMovie.getReleaseDate().toString())));
    }

    @Test
    void getMovieById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/movies/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMovie_WithValidData_ShouldReturnUpdatedMovie() throws Exception {
        MovieDto updateDto = new MovieDto();
        updateDto.setTitle("Updated Title");
        updateDto.setGenre("Comedy");
        updateDto.setReleaseDate(LocalDate.of(2024, 2, 1));

        mockMvc.perform(put("/api/movies/{id}", savedMovie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(updateDto.getTitle())))
                .andExpect(jsonPath("$.genre", is(updateDto.getGenre())))
                .andExpect(jsonPath("$.releaseDate", is(updateDto.getReleaseDate().toString())));
    }

    @Test
    void updateMovie_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(put("/api/movies/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMovieDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMovie_WithExistingId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/movies/{id}", savedMovie.getId()))
                .andExpect(status().isNoContent());

        // Verify movie was deleted
        mockMvc.perform(get("/api/movies/{id}", savedMovie.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMovie_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/movies/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}