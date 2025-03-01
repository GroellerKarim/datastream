# Datastream

> Track your health and fitness data all in one place - your personal health data management app.

## What is Datastream?

Datastream is a comprehensive health and fitness tracking application designed to help you collect, visualize, and analyze your personal health data. The app focuses on four key areas:

- **Heart Rate Monitoring**: Track your heart rate patterns throughout the day
- **Sleep Analysis**: Log and analyze your sleep patterns and quality
- **Workout Tracking**: Record different types of workouts and exercises
- **Habit Building**: Monitor daily habits to build consistency in your health journey

## Tech Stack

Datastream consists of two main components:

### Backend (ds-core)
- **Framework**: Spring Boot 3.3
- **Language**: Java 23
- **Database**: PostgreSQL with Flyway migrations
- **Security**: Spring Security with JWT authentication
- **API Documentation**: SpringDoc OpenAPI
- **Testing**: JUnit, Testcontainers

### Frontend (ds-react)
- **Framework**: React Native with Expo
- **Language**: TypeScript
- **Navigation**: React Navigation
- **HTTP Client**: Axios
- **Date Management**: date-fns
- **Package Manager**: Bun

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 23 or later
- Node.js and Bun
- PostgreSQL database
- Docker (optional, for containerized deployment)

### Backend Setup

1. Navigate to the ds-core directory:
   ```
   cd ds-core
   ```

2. Build the application:
   ```
   ./mvnw clean install
   ```

3. Run the application:
   ```
   ./mvnw spring-boot:run
   ```

The backend server will start on http://localhost:8080 by default.

### Frontend Setup

1. Navigate to the ds-react directory:
   ```
   cd ds-react
   ```

2. Install dependencies:
   ```
   bun install
   ```

3. Start the Expo development server:
   ```
   bun start
   ```

4. Use the Expo Go app on your mobile device to scan the QR code, or press 'a' to open on an Android emulator, or 'i' for iOS simulator.

## Key Features

### Workout Tracking
- Create and manage different workout types
- Track various exercise types including:
  - Sets and reps-based exercises (strength training)
  - Time-based exercises (like planks)
  - Distance-based exercises (running, swimming)
- Monitor workout duration, exercise details, and progress

### Heart Rate Monitoring
- Track resting and active heart rates
- Visualize heart rate trends over time
- Set notifications for abnormal heart rate patterns

### Sleep Analysis
- Log sleep times and duration
- Track sleep quality metrics
- Identify sleep patterns and trends

### Habit Tracking
- Create and monitor daily healthy habits
- Build streaks and track consistency
- Visualize habit performance over time

## Project Structure

The application follows a clean architecture pattern:

### Backend (ds-core)
- **Domain**: Core business entities and logic
- **Persistence**: Database access and entity repositories
- **Service**: Business logic implementation
- **Presentation**: REST API controllers and DTOs
- **Configuration**: Application configuration and security setup

### Frontend (ds-react)
- **Components**: Reusable UI components
- **Screens**: Main application screens organized by feature
- **Navigation**: Application routing and navigation
- **Constants**: Type definitions and theme settings
- **Context**: State management using React Context
- **Config**: Application configuration

## Development Guidelines

Please refer to the `BEST_PRACTICES.md` file in the ds-react directory for detailed frontend development guidelines, including:
- Project structure
- Theming and styling conventions
- Component architecture
- Navigation patterns
- Performance considerations

## License

[Your license information here]

## Contributors

[Your contributor information here] 