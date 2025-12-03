package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! Search for treasure (movies) in our vast collection!
     * This method filters movies based on name, id, and genre parameters.
     * 
     * @param name Movie name to search for (case-insensitive partial match)
     * @param id Specific movie ID to find
     * @param genre Genre to filter by (case-insensitive partial match)
     * @return List of movies matching the search criteria, or empty list if no treasure found
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Arrr! Searching for movie treasures with name: '{}', id: {}, genre: '{}'", name, id, genre);
        
        List<Movie> searchResults = movies.stream()
            .filter(movie -> matchesSearchCriteria(movie, name, id, genre))
            .collect(Collectors.toList());
            
        logger.info("Ahoy! Found {} movie treasures matching yer search criteria", searchResults.size());
        return searchResults;
    }

    /**
     * Helper method to check if a movie matches the search criteria
     * Arrr! This crew member does the heavy lifting for our treasure hunt!
     */
    private boolean matchesSearchCriteria(Movie movie, String name, Long id, String genre) {
        // If searching by ID, it must match exactly
        if (id != null && !movie.getId().equals(id)) {
            return false;
        }
        
        // If searching by name, do case-insensitive partial match
        if (name != null && !name.trim().isEmpty()) {
            if (!movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim())) {
                return false;
            }
        }
        
        // If searching by genre, do case-insensitive partial match
        if (genre != null && !genre.trim().isEmpty()) {
            if (!movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim())) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Get all available genres from our treasure chest
     * Useful for populating search forms, ye savvy?
     */
    public List<String> getAllGenres() {
        return movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
