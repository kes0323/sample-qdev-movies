package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arrr! Unit tests for the MoviesController treasure hunting functionality
 * These tests ensure our controller handles requests ship-shape, matey!
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MockMovieService mockMovieService;
    private ReviewService mockReviewService;

    // Mock MovieService for testing
    private static class MockMovieService extends MovieService {
        private final List<Movie> testMovies;

        public MockMovieService() {
            this.testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                new Movie(3L, "Comedy Movie", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
            );
        }

        @Override
        public List<Movie> getAllMovies() {
            return testMovies;
        }

        @Override
        public Optional<Movie> getMovieById(Long id) {
            return testMovies.stream().filter(m -> m.getId().equals(id)).findFirst();
        }

        @Override
        public List<Movie> searchMovies(String name, Long id, String genre) {
            return testMovies.stream()
                .filter(movie -> {
                    if (id != null && !movie.getId().equals(id)) return false;
                    if (name != null && !name.trim().isEmpty() && 
                        !movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim())) return false;
                    if (genre != null && !genre.trim().isEmpty() && 
                        !movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim())) return false;
                    return true;
                })
                .toList();
        }

        @Override
        public List<String> getAllGenres() {
            return Arrays.asList("Action", "Comedy", "Drama");
        }
    }

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MockMovieService();
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Ahoy! Test getting all movies without search")
    public void testGetMovies() {
        String result = moviesController.getMovies(model, null, null, null);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size());
        assertEquals(false, model.getAttribute("searchPerformed"));
    }

    @Test
    @DisplayName("Shiver me timbers! Test searching movies by name")
    public void testGetMoviesWithNameSearch() {
        String result = moviesController.getMovies(model, "Test", null, null);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals("Test", model.getAttribute("searchName"));
    }

    @Test
    @DisplayName("Batten down the hatches! Test searching movies by ID")
    public void testGetMoviesWithIdSearch() {
        String result = moviesController.getMovies(model, null, 2L, null);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals(2L, movies.get(0).getId());
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals(2L, model.getAttribute("searchId"));
    }

    @Test
    @DisplayName("Yo ho ho! Test searching movies by genre")
    public void testGetMoviesWithGenreSearch() {
        String result = moviesController.getMovies(model, null, null, "Action");
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action", movies.get(0).getGenre());
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals("Action", model.getAttribute("searchGenre"));
    }

    @Test
    @DisplayName("Arrr! Test search with no results")
    public void testGetMoviesWithNoResults() {
        String result = moviesController.getMovies(model, "NonExistent", null, null);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(0, movies.size());
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertNotNull(model.getAttribute("noResultsMessage"));
        assertTrue(((String) model.getAttribute("noResultsMessage")).contains("Arrr!"));
    }

    @Test
    @DisplayName("Chart a course! Test REST API search endpoint - successful search")
    public void testSearchMoviesApiSuccess() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("Test", null, null);
        
        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(true, body.get("success"));
        assertEquals(1, body.get("totalResults"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Ahoy!"));
    }

    @Test
    @DisplayName("Blimey! Test REST API search endpoint - no parameters")
    public void testSearchMoviesApiNoParameters() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, null, null);
        
        assertEquals(400, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(false, body.get("success"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Arrr!"));
        assertTrue(message.contains("at least one search parameter"));
    }

    @Test
    @DisplayName("Scurvy dog! Test REST API search endpoint - invalid ID")
    public void testSearchMoviesApiInvalidId() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, -1L, null);
        
        assertEquals(400, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(false, body.get("success"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Shiver me timbers!"));
        assertTrue(message.contains("positive number"));
    }

    @Test
    @DisplayName("Dead men tell no tales! Test REST API search endpoint - no results")
    public void testSearchMoviesApiNoResults() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("NonExistent", null, null);
        
        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(true, body.get("success"));
        assertEquals(0, body.get("totalResults"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Arrr!"));
        assertTrue(message.contains("No movie treasures found"));
    }

    @Test
    @DisplayName("Treasure hunt! Test REST API search endpoint - combined parameters")
    public void testSearchMoviesApiCombinedParameters() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("Action", null, "Action");
        
        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(true, body.get("success"));
        assertEquals(1, body.get("totalResults"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchParams = (Map<String, Object>) body.get("searchParameters");
        assertEquals("Action", searchParams.get("name"));
        assertEquals("Action", searchParams.get("genre"));
        assertNull(searchParams.get("id"));
    }

    @Test
    @DisplayName("Ship shape! Test getting movie details")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    @DisplayName("Walk the plank! Test getting movie details - not found")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    @Test
    @DisplayName("All hands on deck! Test movie service integration")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        List<String> genres = mockMovieService.getAllGenres();
        assertEquals(3, genres.size());
        assertTrue(genres.contains("Drama"));
    }
}
