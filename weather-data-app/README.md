# Weather Data Application

A Spring Boot application that integrates with the OpenWeatherMap API to retrieve, cache, and manage weather data. The application supports both city names and ZIP codes for weather queries and provides automatic periodic data refresh using Spring Scheduler.

## 🚀 Features

- **Weather Data Retrieval**: Search weather by city name or ZIP code
- **Smart Caching**: 30-minute cache validity with automatic refresh
- **Automatic Updates**: Scheduled refresh of tracked locations every 30 minutes
- **Data Persistence**: H2 in-memory database with JPA/Hibernate
- **RESTful API**: Comprehensive REST endpoints for all operations
- **Validation**: Input validation with detailed error messages
- **Logging**: Logging for debugging and monitoring

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Language**: Java 21
- **Database**: H2 (in-memory)
- **ORM**: Hibernate/JPA
- **HTTP Client**: Spring Cloud OpenFeign
- **Scheduler**: Spring Scheduler
- **Validation**: Jakarta Validation (Bean Validation)
- **Build Tool**: Maven
- **External API**: OpenWeatherMap API

## 🏗 Architecture

The application follows clean architecture principles with clear separation of concerns:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │    │    Services     │    │  Repositories   │
│                 │    │                 │    │                 │
│WeatherController│────│ WeatherService  │────│WeatherDataRepo  │
│                 │    │ CacheService    │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌─────────────────┐              │
         └──────────────│   Scheduler     │──────────────┘
                        │                 │
                        │WeatherScheduler │
                        └─────────────────┘
```

**Key Components:**
- **Controllers**: Handle HTTP requests and responses
- **Services**: Business logic and coordination
- **Cache Service**: Dedicated caching logic
- **Repositories**: Data access layer
- **Scheduler**: Periodic data refresh
- **DTOs**: Data transfer objects
- **Entities**: JPA entities

## 📋 Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **OpenWeatherMap API Key** (free at https://openweathermap.org/api)
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code (recommended)

## 📦 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Frodlik/ai-practise.git
cd weather-data-app
```

### 2. API Key Setup

#### Using Environment Variables
```bash
# Linux/Mac
export API_KEY=your_openweathermap_api_key_here

# Windows
set API_KEY=your_openweathermap_api_key_here
```

### 3. Build the Application
```bash
./mvnw clean compile
```

## 🚀 Running the Application

### Development Mode
```bash
./mvnw spring-boot:run
```

### Using IDE (IntelliJ IDEA)
1. Open the project in IntelliJ IDEA
2. Navigate to `Run/Debug Configurations`
3. Add Environment Variable: `API_KEY=your_api_key`
4. Run `WeatherDataAppApplication`

### Production Mode
```bash
./mvnw clean package
java -jar target/weather-data-app-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Weather Data Endpoints

#### Get Weather by City
```http
GET /api/weather/city/{cityName}
```
**Example**: `GET /api/weather/city/London`

#### Get Weather by ZIP Code
```http
GET /api/weather/zip/{zipCode}?country=US
```
**Example**: `GET /api/weather/zip/10001?country=US`

#### Universal Search
```http
POST /api/weather/search
Content-Type: application/json

{
  "city": "London"
}
```

### Information Endpoints

#### List Tracked Cities
```http
GET /api/weather/tracked/cities
```

#### List Tracked ZIP Codes
```http
GET /api/weather/tracked/zipcodes
```

#### Health Check
```http
GET /api/weather/health
```

### Sample Response Format

```json
{
  "city": "London",
  "country": "GB",
  "zipCode": null,
  "temperature": 15.23,
  "feelsLike": 14.82,
  "humidity": 72,
  "pressure": 1013,
  "description": "scattered clouds",
  "mainWeather": "Clouds",
  "windSpeed": 3.2,
  "windDirection": 190,
  "lastUpdated": "2025-08-25T12:00:00",
  "fromCache": false,
  "temperatureUnit": "°C",
  "windSpeedUnit": "m/s",
  "pressureUnit": "hPa",
  "humidityUnit": "%"
}
```

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### Test Coverage
The project maintains **>80% test coverage** across:
- Unit tests for all service methods
- Integration tests for REST endpoints
- Repository tests for custom queries
- Cache service tests

### Test Structure
```
src/test/java/
├── com/demo/weatherdataapp/
│   ├── controller/     # Controller tests
│   ├── service/        # Service unit tests
│   ├── repository/     # Repository tests
│   └── scheduler/      # Scheduler tests

```

## 💾 Database Access

### H2 Console Access
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:weather_db`
- **Username**: `sa`
- **Password**: `password`

### Database Schema
```sql
-- Weather data table
CREATE TABLE weather_data (
    id BIGINT PRIMARY KEY,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(10),
    zip_code VARCHAR(20),
    temperature DOUBLE,
    feels_like DOUBLE,
    humidity INTEGER,
    pressure INTEGER,
    description VARCHAR(255),
    main_weather VARCHAR(100),
    wind_speed DOUBLE,
    wind_direction INTEGER,
    timestamp TIMESTAMP,
    last_updated TIMESTAMP
);
```

## 🤝 Development Feedback

### Task Completion Analysis

#### Was it easy to complete the task using AI?
**Yes, very easy.** The AI assistant provided comprehensive guidance throughout the development process. The conversational approach allowed for iterative refinement and addressing specific requirements as they arose. The AI understood the context well and provided relevant solutions.

#### How long did the task take to complete?
**Approximately 4-5 hours** of active development time, spread across multiple sessions:
- Initial setup and basic structure: 1 hour
- Core functionality implementation: 2 hours  
- Refactoring and improvements: 1 hour
- Testing and documentation: 1 hour

#### Was the code ready to run after generation?
**Mostly yes, with minor adjustments needed:**

**What worked immediately:**
- Basic Spring Boot structure and configuration
- JPA entities and repositories
- REST controller endpoints
- Service layer implementation

**What required changes:**
- API key configuration troubleshooting (.env file handling)
- Fine-tuning of validation annotations
- Adjustment of Feign client configuration
- Integration test setup and mocking

**Specific adjustments made:**
- Changed from `@Component` to `@Configuration` in configuration classes
- Added proper error handling for API failures
- Implemented custom exception classes
- Fixed validation constraints in DTOs

#### Which challenges did you face during completion?
1. **Configuration Management**: Initially struggled with proper `.env` file integration with Spring Boot
2. **Feign Client Setup**: Required understanding of proper Feign configuration with OpenWeatherMap API
3. **Cache Strategy**: Deciding on the right balance between performance and data freshness
5. **Testing Strategy**: Setting up proper test structure with mocks for external API calls

#### Which specific prompts learned as good practice?
1. **Incremental Development**: "Let's start with basic structure and add features gradually"
2. **Specific Problem Description**: "The API key shows as %24%7BAPI_KEY%7D in the URL, which means..."
3. **Code Review Requests**: "Can you review this code for better practices?"
4. **Architecture Guidance**: "What's the best way to separate concerns between cache and API calls?"
5. **Refactoring Requests**: "Can we extract the cache logic into a separate service?"

### Key Learnings
- AI excels at generating boilerplate code and providing architectural guidance
- Interactive refinement works better than trying to get perfect code in one shot
- Specific error messages help AI provide targeted solutions
- Asking for explanations improves understanding and code quality
- Breaking complex tasks into smaller, focused requests yields better results

## 🙏 Acknowledgments

- [OpenWeatherMap](https://openweathermap.org/) for providing the weather API
- Spring Boot team for the excellent framework
- Claude AI for development assistance and guidance

---

**Note**: This application was developed as a demonstration of Spring Boot best practices and AI-assisted development. The conversation logs with the AI assistant are available in `chat.log` for reference.