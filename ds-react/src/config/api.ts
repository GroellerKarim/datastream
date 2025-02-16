// In development, use your local backend URL
export const API_BASE_URL = 'http://192.168.0.81:8080';

export const API_ENDPOINTS = {
  LOGIN: `${API_BASE_URL}/api/v1/users/login`,
  REGISTER: `${API_BASE_URL}/api/v1/users/register`,
  USER_INFO: `${API_BASE_URL}/api/v1/users/token`,
  WORKOUT_TYPES: `${API_BASE_URL}/api/v1/workouts/types`,
  WORKOUTS: `${API_BASE_URL}/api/v1/workouts`,
  EXERCISE_DEFINITIONS: `${API_BASE_URL}/api/v1/exercises/all`,
  RECENT_EXERCISES: (workoutTypeId: number) => `${API_BASE_URL}/api/v1/exercises/recent/${workoutTypeId}`,
} as const; 