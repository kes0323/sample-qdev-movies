package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arrr! Unit tests for the MovieService treasure hunting functionality
 * These tests ensure our search methods work ship-shape, matey!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Ahoy! Test searching by movie name - case insensitive")
    public void testSearchMoviesByName() {
        // Test case-insensitive partial match
        List<Movie> results = movieService.searchMovies("prison", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test lowercase search
        results = movieService.searchMovies("family", null, null);
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());

        // Test uppercase search
        results = movieService.searchMovies("MASKED", null, null);
        assertEquals(1, results.size());
        assertEquals("The Masked Hero", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Shiver me timbers! Test searching by movie ID")
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test non-existent ID
        results = movieService.searchMovies(null, 999L, null);
        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("Batten down the hatches! Test searching by genre")
    public void testSearchMoviesByGenre() {
        // Test exact genre match
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertTrue(results.size() >= 1);
        assertTrue(results.stream().anyMatch(m -> m.getGenre().contains("Drama")));

        // Test partial genre match
        results = movieService.searchMovies(null, null, "Crime");
        assertTrue(results.size() >= 1);
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("crime")));

        // Test case-insensitive genre search
        results = movieService.searchMovies(null, null, "action");
        assertTrue(results.size() >= 1);
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("action")));
    }

    @Test
    @DisplayName("Yo ho ho! Test combined search criteria")
    public void testSearchMoviesByCombinedCriteria() {
        // Search by name and genre
        List<Movie> results = movieService.searchMovies("Family", null, "Crime");
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
        assertTrue(results.get(0).getGenre().contains("Crime"));

        // Search with no matches
        results = movieService.searchMovies("NonExistent", null, "Comedy");
        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("Arrr! Test empty and null search parameters")
    public void testSearchMoviesWithEmptyParameters() {
        // All null parameters should return all movies
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertEquals(movieService.getAllMovies().size(), results.size());

        // Empty strings should be treated as null
        results = movieService.searchMovies("", null, "");
        assertEquals(movieService.getAllMovies().size(), results.size());

        // Whitespace-only strings should be treated as empty
        results = movieService.searchMovies("   ", null, "   ");
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Chart a course! Test getting all genres")
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        assertNotNull(genres);
        assertTrue(genres.size() > 0);
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Action/Crime"));
        
        // Ensure genres are sorted
        for (int i = 1; i < genres.size(); i++) {
            assertTrue(genres.get(i-1).compareTo(genres.get(i)) <= 0);
        }
    }

    @Test
    @DisplayName("Blimey! Test edge cases for search")
    public void testSearchEdgeCases() {
        // Test with very long search string
        List<Movie> results = movieService.searchMovies("This is a very long movie name that definitely does not exist", null, null);
        assertEquals(0, results.size());

        // Test with special characters
        results = movieService.searchMovies("@#$%", null, null);
        assertEquals(0, results.size());

        // Test with negative ID (should be handled gracefully)
        results = movieService.searchMovies(null, -1L, null);
        assertEquals(0, results.size());

        // Test with zero ID
        results = movieService.searchMovies(null, 0L, null);
        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("Treasure hunt! Test partial name matching")
    public void testPartialNameMatching() {
        // Test partial word matching
        List<Movie> results = movieService.searchMovies("War", null, null);
        assertTrue(results.stream().anyMatch(m -> m.getMovieName().contains("Wars")));

        // Test single character
        results = movieService.searchMovies("T", null, null);
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(m -> m.getMovieName().toLowerCase().contains("t")));
    }
}