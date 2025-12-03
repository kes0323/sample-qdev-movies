# Movie Search API Documentation 🏴‍☠️

Ahoy matey! Welcome to the Movie Search API documentation. This treasure map will guide ye through using our REST API to search for movie treasures!

## Base URL
```
http://localhost:8080
```

## Authentication
No authentication required - this be a free treasure hunt for all!

## Endpoints

### 1. Search Movies
**Arrr!** The main treasure hunting endpoint for finding movies.

```http
GET /movies/search
```

#### Parameters
At least one parameter be required, ye savvy?

| Parameter | Type   | Required | Description |
|-----------|--------|----------|-------------|
| `name`    | string | No       | Search by movie name (case-insensitive partial match) |
| `id`      | number | No       | Search by specific movie ID (must be positive) |
| `genre`   | string | No       | Filter by genre (case-insensitive partial match) |

#### Response Format

**Success Response (200 OK):**
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
      "description": "Two imprisoned men bond over a number of years...",
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

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Arrr! Ye need to provide at least one search parameter, matey! (name, id, or genre)",
  "movies": [],
  "totalResults": 0
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Arrr! Something went wrong during the treasure hunt. Try again later, matey!",
  "movies": [],
  "totalResults": 0
}
```

#### Examples

**Search by movie name:**
```bash
curl "http://localhost:8080/movies/search?name=prison"
```

**Search by genre:**
```bash
curl "http://localhost:8080/movies/search?genre=action"
```

**Search by ID:**
```bash
curl "http://localhost:8080/movies/search?id=1"
```

**Combined search:**
```bash
curl "http://localhost:8080/movies/search?name=family&genre=crime"
```

**JavaScript example:**
```javascript
fetch('/movies/search?name=prison&genre=drama')
  .then(response => response.json())
  .then(data => {
    if (data.success) {
      console.log(`Ahoy! Found ${data.totalResults} movies:`, data.movies);
    } else {
      console.error('Search failed:', data.message);
    }
  });
```

## Available Genres
The following genres be available in our treasure chest:

- Action/Crime
- Action/Sci-Fi
- Adventure/Fantasy
- Adventure/Sci-Fi
- Crime/Drama
- Drama
- Drama/History
- Drama/Romance
- Drama/Thriller

## Search Behavior

### Name Search
- **Case Insensitive**: "PRISON" matches "The Prison Escape"
- **Partial Match**: "War" matches "Space Wars: The Beginning"
- **Word Boundaries**: Searches within movie titles

### Genre Search
- **Case Insensitive**: "action" matches "Action/Crime"
- **Partial Match**: "Crime" matches "Crime/Drama"
- **Multi-genre Support**: Handles genres with slashes like "Action/Crime"

### ID Search
- **Exact Match**: Only returns the movie with the specified ID
- **Validation**: Must be a positive integer
- **Range**: Valid IDs are 1-12 (based on current movie data)

### Combined Search
When multiple parameters are provided, movies must match ALL criteria (AND logic):
- `name=family&genre=crime` returns movies that contain "family" in the name AND have "crime" in the genre

## Error Handling

### Common Error Scenarios

1. **No Search Parameters**
   - Status: 400 Bad Request
   - Message: "Arrr! Ye need to provide at least one search parameter, matey!"

2. **Invalid Movie ID**
   - Status: 400 Bad Request
   - Message: "Shiver me timbers! Movie ID must be a positive number, ye scurvy dog!"

3. **No Results Found**
   - Status: 200 OK (not an error)
   - Message: "Arrr! No movie treasures found matching yer search criteria."
   - Empty movies array

4. **Server Error**
   - Status: 500 Internal Server Error
   - Message: "Arrr! Something went wrong during the treasure hunt."

## Rate Limiting
Currently no rate limiting be implemented - search to yer heart's content!

## Response Headers
```
Content-Type: application/json
```

## Movie Data Model

Each movie object contains the following fields:

| Field        | Type   | Description |
|--------------|--------|-------------|
| `id`         | number | Unique movie identifier |
| `movieName`  | string | Full movie title |
| `director`   | string | Movie director name |
| `year`       | number | Release year |
| `genre`      | string | Movie genre(s) |
| `description`| string | Movie plot description |
| `duration`   | number | Runtime in minutes |
| `imdbRating` | number | Rating out of 5.0 |
| `icon`       | string | Movie emoji icon |

## Integration Examples

### Python
```python
import requests

def search_movies(name=None, movie_id=None, genre=None):
    params = {}
    if name:
        params['name'] = name
    if movie_id:
        params['id'] = movie_id
    if genre:
        params['genre'] = genre
    
    response = requests.get('http://localhost:8080/movies/search', params=params)
    return response.json()

# Usage
results = search_movies(name='prison', genre='drama')
print(f"Found {results['totalResults']} movies")
```

### Java
```java
// Using Spring's RestTemplate
RestTemplate restTemplate = new RestTemplate();
String url = "http://localhost:8080/movies/search?name=prison&genre=drama";
ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
Map<String, Object> result = response.getBody();
```

### Node.js
```javascript
const axios = require('axios');

async function searchMovies(params) {
  try {
    const response = await axios.get('http://localhost:8080/movies/search', { params });
    return response.data;
  } catch (error) {
    console.error('Search failed:', error.response.data);
    throw error;
  }
}

// Usage
searchMovies({ name: 'prison', genre: 'drama' })
  .then(data => console.log(`Found ${data.totalResults} movies`));
```

## Testing the API

### Using curl
```bash
# Test successful search
curl -i "http://localhost:8080/movies/search?name=prison"

# Test error case
curl -i "http://localhost:8080/movies/search"

# Test invalid ID
curl -i "http://localhost:8080/movies/search?id=-1"
```

### Using Postman
1. Create a new GET request
2. Set URL to `http://localhost:8080/movies/search`
3. Add query parameters in the Params tab
4. Send the request and examine the JSON response

## Changelog

### Version 1.0.0
- Initial release of movie search API
- Support for name, ID, and genre search parameters
- Pirate-themed response messages
- Comprehensive error handling
- Case-insensitive partial matching for text fields

---

**Arrr!** May fair winds fill yer API calls, and may ye find all the movie treasures ye seek! 🏴‍☠️⚓

For questions or issues, check the application logs for pirate-themed error messages, ye savvy?