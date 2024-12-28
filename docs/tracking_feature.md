# Workout Tracking Feature

## Core Features

### 1. Workout Duration Tracking
- Real-time timer display showing total workout duration
- No pause functionality - workouts are treated as continuous sessions
- Duration tracked from start to completion/cancellation

### 2. Exercise/Set Tracking
- Track start/end times for exercises and individual sets
- Automatically track rest times between sets and exercises
- Allow sets to be marked as failure/success
- Show last 2 exercise records of current exercise being performed

### 3. Exercise Selection
- Prioritize displaying previously used exercises first
- Provide option to select new exercises from full library
- Build workout progressively during tracking session

### 4. UI Elements
- Rest timer showing time since last set completion
- Prominent workout duration display
- Cancel workout button with confirmation dialog
- Clear exercise/set input controls

## Future Features (Not Implementing Now)
- Workout plans (FEAT-102)
- Exercise and set notes (FEAT-101)
- Local workout progress storage (FEAT-103)
- Min/max validation for weights/reps/duration
- Rest time warnings
- Recommended rest time indicators
- Workout pause functionality

## Data Model Requirements
- Track workout start/end times
- Track exercise start/end times
- Track set start/end times
- Store set failure status
- Calculate rest periods between sets
- Link exercises to workout session

## User Flow
1. User starts new workout tracking session
2. User selects exercise to perform
3. User records sets for exercise (reps, weight, failure status)
4. Rest timer runs between sets
5. User continues with next exercise
6. User completes or cancels workout 