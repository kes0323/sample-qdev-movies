package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "id", required = false) Long id,
                           @RequestParam(value = "genre", required = false) String genre) {
        logger.info("Ahoy! Fetching movies with search parameters - name: '{}', id: {}, genre: '{}'", name, id, genre);
        
        List<Movie> movies;
        boolean isSearchRequest = (name != null && !name.trim().isEmpty()) || 
                                 id != null || 
                                 (genre != null && !genre.trim().isEmpty());
        
        if (isSearchRequest) {
            movies = movieService.searchMovies(name, id, genre);
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            
            if (movies.isEmpty()) {
                model.addAttribute("noResultsMessage", "Arrr! No treasure found matching yer search criteria, matey!");
            } else {
                model.addAttribute("resultsMessage", String.format("Ahoy! Found %d movie treasures for ye!", movies.size()));
            }
        } else {
            movies = movieService.getAllMovies();
            model.addAttribute("searchPerformed", false);
        }
        
        model.addAttribute("movies", movies);
        model.addAttribute("availableGenres", movieService.getAllGenres());
        return "movies";
    }

    /**
     * Arrr! REST endpoint for searching movie treasures
     * Returns JSON response for API clients, ye savvy?
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Arrr! API search request received - name: '{}', id: {}, genre: '{}'", name, id, genre);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate that at least one search parameter is provided
            if ((name == null || name.trim().isEmpty()) && id == null && (genre == null || genre.trim().isEmpty())) {
                response.put("success", false);
                response.put("message", "Arrr! Ye need to provide at least one search parameter, matey! (name, id, or genre)");
                response.put("movies", List.of());
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate ID parameter if provided
            if (id != null && id <= 0) {
                response.put("success", false);
                response.put("message", "Shiver me timbers! Movie ID must be a positive number, ye scurvy dog!");
                response.put("movies", List.of());
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            response.put("success", true);
            response.put("movies", searchResults);
            response.put("totalResults", searchResults.size());
            
            if (searchResults.isEmpty()) {
                response.put("message", "Arrr! No movie treasures found matching yer search criteria. Try different parameters, matey!");
            } else {
                response.put("message", String.format("Ahoy! Found %d movie treasure%s for ye!", 
                    searchResults.size(), searchResults.size() == 1 ? "" : "s"));
            }
            
            // Add search parameters to response for reference
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("name", name);
            searchParams.put("id", id);
            searchParams.put("genre", genre);
            response.put("searchParameters", searchParams);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Blimey! Error occurred during movie search: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Arrr! Something went wrong during the treasure hunt. Try again later, matey!");
            response.put("movies", List.of());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}