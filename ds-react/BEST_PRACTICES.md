# React Native App Best Practices

## 1. Project Structure
```
src/
├── components/     # Reusable UI components
├── constants/      # App-wide constants and themes
├── navigation/     # Navigation configuration
└── screens/        # Screen components
```

## 2. Theming and Styling
- **Centralized Theme System** (`theme.ts`)
  - Consistent color palette
  - Typography system with predefined sizes and weights
  - Standardized spacing scale
  - Layout constants (border radius, icon sizes)
  - All values are strictly typed with `as const`

## 3. Component Architecture
- **Type Safety**
  - TypeScript for all components
  - Proper type definitions for props
  - Usage of React.FC for functional components

- **Component Structure**
  - Clear prop interfaces
  - Separation of concerns
  - Local StyleSheet definitions
  - Consistent use of theme constants

## 4. Navigation
- **Bottom Tab Navigation**
  - Clean navigation structure using `@react-navigation/bottom-tabs`
  - Stack navigation for nested routes
  - Type-safe navigation props
  - Consistent icon system using Expo vector icons

## 5. Styling Patterns
- **Consistent Styling Approach**
  - Use of StyleSheet.create for performance
  - Theme-based styling
  - Proper shadow handling for both iOS and Android
  - Responsive layouts using flexbox

## 6. UI/UX Considerations
- Safe area insets for different devices
- Proper touch feedback using TouchableOpacity
- Consistent spacing and typography
- Proper image handling with resize modes

## 7. Code Organization
- Clear file naming conventions
- Separation of concerns between components
- Modular and reusable components
- Proper type exports and imports

## 8. Development Environment
- Using Bun as the JavaScript runtime
- Expo for cross-platform development
- Proper gitignore configuration
- TypeScript for type safety

## 9. Performance Considerations
- Proper use of memo and callbacks where needed
- StyleSheet for optimized styles
- Efficient image loading and caching
- Proper navigation configuration

## 10. Asset Management
- Organized constants for images and theme
- Proper handling of static assets
- Consistent icon usage through Expo vector icons 