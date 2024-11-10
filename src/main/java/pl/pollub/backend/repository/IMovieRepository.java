package pl.pollub.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pollub.backend.model.Movie;

@Repository
public interface IMovieRepository extends JpaRepository<Movie, Long> {
}

