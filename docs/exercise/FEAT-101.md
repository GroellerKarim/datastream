# [FEAT-101] Add Notes Feature for Workout Tracking

## Description
Add the ability for users to write optional notes for exercises and individual sets during workout tracking.

## Technical Details
- Add `notes` field to `ExerciseRecord` and `ExerciseSet` entities
- Update database schema with new columns
- Add notes input field in tracking UI (collapsible/expandable)
- Include notes in workout summary view
- Sync notes with backend

## Acceptance Criteria
- Users can add notes to exercises during tracking
- Users can add notes to individual sets during tracking
- Notes are persisted and viewable in workout history
- Notes field is optional
- Notes support multiline text

## Story Points: 3

## Priority: Medium

## Dependencies
- None (can be implemented independently after base tracking functionality) 