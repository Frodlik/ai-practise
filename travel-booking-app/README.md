# Travel Booking Application

Travel booking platform built with **Spring Boot** that allows users to search for flights, hotels, and rental cars. The application integrates with multiple third-party services including **Amadeus API** for flights and hotels, and **Priceline API** (via RapidAPI) for car rentals to provide real-time availability and pricing information.

## Features

- **Flight Search**: Search for flights using Amadeus API with real-time pricing and availability
- **Hotel Search**: Find hotels with detailed information including ratings, addresses, and pricing
- **Car Rental Search**: Search for rental cars with multiple pickup/dropoff locations
- **Multi-Provider Integration**: Seamlessly integrates with multiple travel service providers

## Architecture

The application follows a clean architecture pattern with:

- **Provider Pattern**: Abstracted interfaces for different service providers
- **DTO Pattern**: Data Transfer Objects for clean API contracts
- **Service Layer**: Business logic separation
- **Configuration Management**: Externalized configuration for API keys and endpoints
- **Feign Clients**: Declarative HTTP clients for external API integration

### Supported Providers

1. **Amadeus API**
    - Flight search and booking
    - Hotel search and availability

2. **Priceline API (via RapidAPI)**
    - Car rental search
    - Location-based services

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Active API keys for:
    - Amadeus API (for flights and hotels)
    - RapidAPI (for Priceline car rentals)

## Configuration

### Required Environment Variables / Application Properties

```properties
# Amadeus API Configuration
amadeus:
    base-url: ${AMADEUS_BASE_URL:https://test.api.amadeus.com}
    client-id: ${AMADEUS_CLIENT_ID}
    client-secret: ${AMADEUS_CLIENT_SECRET}

# RapidAPI (Priceline Cars) Configuration
car-api:
    base-url: ${CAR_API_BASE_URL:https://priceline-com-provider.p.rapidapi.com}
    key: ${CAR_API_KEY}
    host: ${CAR_API_HOST:priceline-com-provider.p.rapidapi.com}
```

### API Keys Setup

1. **Amadeus API**:
    - Register at [Amadeus for Developers](https://developers.amadeus.com/)
    - Create a new application
    - Get your Client ID and Client Secret

2. **RapidAPI (Priceline)**:
    - Register at [RapidAPI](https://rapidapi.com/)
    - Subscribe to Priceline API
    - Get your API key

## Installation & Running

### 1. Clone the Repository
```bash
git clone https://github.com/Frodlik/ai-practise.git
cd travel-booking-app
```

### 2. Configure Application Properties
Create `application.yml` or `application.properties` file in `src/main/resources/` with the required configuration values.

### 3. Build the Application
```bash
mvn clean compile
```

### 4. Run Tests
```bash
mvn test
```

### 5. Start the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Flight Search
```http
POST /api/flights/search
Content-Type: application/json

{
  "origin": "NYC",
  "destination": "LAX", 
  "departureDate": "2025-09-15",
  "adults": 2
}
```

### Hotel Search
```http
POST /api/hotels/search
Content-Type: application/json

{
  "cityCode": "PAR",
  "checkIn": "2025-09-15",
  "checkOut": "2025-09-18", 
  "adults": 2
}
```

### Car Rental Search
```http
POST /api/cars/search
Content-Type: application/json

{
  "pickupLocation": "New York",
  "dropoffLocation": "New York",
  "pickupDateTime": "2025-09-15T10:00:00",
  "dropoffDateTime": "2025-09-18T10:00:00"
}
```

## Key Design Patterns

1. **Strategy Pattern**: Different provider implementations
2. **Factory Pattern**: Provider selection based on configuration
3. **Builder Pattern**: Request object construction
4. **Adapter Pattern**: External API response mapping

## Error Handling

The application implements comprehensive error handling:
- Graceful fallbacks when external APIs are unavailable
- Detailed logging for debugging
- User-friendly error responses
- Timeout and retry mechanisms

## Troubleshooting

### Common Issues

1. **API Key Authentication Errors**
    - Verify API keys are correctly configured
    - Check API subscription status
    - Ensure proper header formatting

2. **Connection Timeouts**
    - Check network connectivity
    - Verify external API endpoints are accessible
    - Consider increasing timeout configurations

3. **Date Format Issues**
    - Ensure date formats match API requirements
    - Check timezone handling in requests

## Development Feedback

### AI-Assisted Development Experience

#### Was it easy to complete the task using AI?
Yes, using AI significantly accelerated the development process. The AI helped with:
- Generating boilerplate code for DTOs and service classes
- Creating proper Feign client configurations
- Implementing error handling patterns
- Writing comprehensive documentation

#### How long did the task take to complete?
Approximately 8-10 hours total:
- 3 hours for initial project setup and API integration
- 2 hours for implementing provider patterns
- 2 hours for error handling and testing
- 1-2 hours for documentation and refinement

#### Was the code ready to run after generation?
About 80% of the generated code was ready to run immediately. Changes required:
- Fine-tuning API request/response mappings
- Adjusting date/time formatting for different APIs
- Adding proper null checks and error handling
- Configuring Feign client timeouts and retry logic

#### Challenges faced during completion:
1. **API Documentation Inconsistencies**: Different providers had varying request/response formats
2. **Date Format Variations**: Each API expected different date/time formats
3. **Error Response Handling**: Mapping different error response structures from various APIs
4. **Rate Limiting**: Implementing proper rate limiting and retry mechanisms
5. **Testing External APIs**: Creating proper mocks for integration testing

#### Specific prompts learned as good practice:
1. **"Generate a Spring Boot Feign client for [API] with proper error handling"** - Produced clean, production-ready clients
2. **"Create a mapper method that safely handles null values for [object type]"** - Generated robust mapping code
3. **"Implement retry logic with exponential backoff for external API calls"** - Created resilient service calls
4. **"Generate comprehensive unit tests with 80%+ coverage for [service class]"** - Produced thorough test suites
5. **"Create configuration properties for [service] with validation"** - Generated proper Spring configuration classes

**Note**: This is a demonstration application. For production use, implement additional security measures, monitoring, and compliance requirements.