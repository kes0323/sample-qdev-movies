# Movie Service - Spring Boot Demo Application 🏴‍☠️

Ahoy matey! A pirate-themed movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a swashbuckling twist!

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **🔍 Movie Search & Filtering**: Search for movie treasures by name, ID, or genre with our new pirate-themed search interface
- **📡 REST API**: JSON API endpoint for developers to search movies programmatically
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds, smooth animations, and pirate styling
- **🏴‍☠️ Pirate Language**: All search functionality includes fun pirate language and terminology

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for HTML templating

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List with Search**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Search API**: http://localhost:8080/movies/search?name=prison&genre=drama

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Business logic with search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review service
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Enhanced movie list with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Comprehensive unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MovieServiceTest.java         # Tests for search functionality
            └── MoviesControllerTest.java     # Tests for REST endpoints
```

## API Endpoints

### Get All Movies (with optional search)
```
GET /movies
```
Returns an HTML page displaying movies with an interactive search form. Supports optional query parameters for filtering.

**Query Parameters (optional):**
- `name` (string): Search by movie name (case-insensitive partial match)
- `id` (number): Search by specific movie ID
- `genre` (string): Filter by genre (case-insensitive partial match)

**Examples:**
```
http://localhost:8080/movies                           # All movies
http://localhost:8080/movies?name=prison               # Movies with "prison" in name
http://localhost:8080/movies?genre=drama               # Drama movies
http://localhost:8080/movies?name=family&genre=crime   # Combined search
```

### 🆕 Movie Search API (JSON Response)
```
GET /movies/search
```
**Arrr!** Returns JSON response with search results. Perfect for API clients and developers, ye savvy!

**Query Parameters (at least one required):**
- `name` (string): Search by movie name (case-insensitive partial match)
- `id` (number): Search by specific movie ID (must be positive)
- `genre` (string): Filter by genre (case-insensitive partial match)

**Response Format:**
```json
{
  "success": true,
  "message": "Ahoy! Found 2 movie treasures for ye!",
  "totalResults": 2,
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond...",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ],
  "searchParameters": {
    "name": "prison",
    "id": null,
    "genre": "drama"
  }
}
```

**Examples:**
```bash
# Search by name
curl "http://localhost:8080/movies/search?name=prison"

# Search by genre
curl "http://localhost:8080/movies/search?genre=action"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Combined search
curl "http://localhost:8080/movies/search?name=family&genre=crime"
```

**Error Responses:**
- `400 Bad Request`: Missing search parameters or invalid ID
- `500 Internal Server Error`: Server error during search

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## Search Features

### 🔍 Interactive Search Form
The main movies page now includes a pirate-themed search form with:
- **Movie Name**: Text input for partial name matching
- **Movie ID**: Number input for exact ID lookup
- **Genre**: Dropdown with all available genres
- **Search Button**: Triggers the search with pirate flair
- **Clear Button**: Resets the form and shows all movies

### 🏴‍☠️ Pirate Language Integration
All search functionality includes authentic pirate language:
- Success messages: "Ahoy! Found X movie treasures for ye!"
- Error messages: "Arrr! No treasure found matching yer search criteria, matey!"
- API responses: "Shiver me timbers! Movie ID must be a positive number, ye scurvy dog!"
- Logging: "Arrr! Searching for movie treasures..."

### Edge Case Handling
- **Empty Results**: Friendly pirate message when no movies match
- **Invalid Parameters**: Proper validation with pirate-themed error messages
- **Missing Parameters**: API requires at least one search parameter
- **Case Insensitive**: All text searches work regardless of case
- **Partial Matching**: Name and genre searches support partial matches

## Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest
mvn test -Dtest=MoviesControllerTest

# Run with coverage
mvn test jacoco:report
```

### Test Coverage
- **MovieServiceTest**: Tests all search functionality, edge cases, and genre retrieval
- **MoviesControllerTest**: Tests both HTML and JSON endpoints, error handling, and parameter validation
- **Pirate Language**: Tests ensure all pirate-themed messages are properly displayed

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

Check the logs for pirate-themed error messages:
```bash
tail -f logs/application.log | grep -i "arrr\|ahoy\|matey"
```

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate themes
- Add new search features (rating range, year range, etc.)
- Improve the responsive design
- Add more pirate language and terminology
- Implement advanced filtering options

### Adding New Movies

Edit `src/main/resources/movies.json` and add new movie objects following the existing format.

### Extending Search Functionality

The search system is designed to be easily extensible. Add new search criteria by:
1. Adding parameters to `MovieService.searchMovies()`
2. Updating the `matchesSearchCriteria()` method
3. Adding form fields to `movies.html`
4. Adding corresponding tests

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

**Arrr!** Happy treasure hunting, ye landlubbers! 🏴‍☠️⚓🎬
