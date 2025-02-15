export type ExerciseRecord = {
  exerciseId: number;
  exerciseDefinitionId: number;
  sets: number;
  reps: number;
  weight: number;
  notes?: string;
};

export enum DistanceUnit {
  METERS = 'METERS',
  KILOMETERS = 'KILOMETERS',
  MILES = 'MILES'
}

export enum ExerciseType {
  DISTANCE = 'DISTANCE',
  SETS_REPS = 'SETS_REPS',
  SETS_TIME = 'SETS_TIME'
}

export type ExerciseSetResponse = {
  startTime: string; 
  endTime: string; 
  failure: boolean;
  repetitions: number;
  weightKg: number;
};

export type ExerciseRecordDetailsResponse = {
  distance?: number;
  distanceUnit?: DistanceUnit;
  sets?: ExerciseSetResponse[];
  weightKg?: number;
};

export type ExerciseRecordResponse = {
  exerciseRecordId: number;
  exerciseDefinitionId: number;
  exerciseName: string;
  type: ExerciseType;
  startTime: string; 
  endTime: string; 
  details: ExerciseRecordDetailsResponse;
  orderIndex: number;
};

export type WorkoutResponse = {
  workoutId: number;
  durationMs: number;
  date: string; 
  exercises: ExerciseRecordResponse[];
  workoutType: string;
};

export type WorkoutListResponse = {
  content: WorkoutResponse[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  last: boolean;
  first: boolean;
  empty: boolean;
}; 