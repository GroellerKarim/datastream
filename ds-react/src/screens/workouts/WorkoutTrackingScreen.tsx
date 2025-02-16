import React, { useState, useEffect } from 'react';
import { View, StyleSheet, Text, TouchableOpacity, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { colors, spacing, typography, borderRadius, shadows } from '../../constants/theme';
import { ExerciseType, DistanceUnit, WorkoutTypeResponse, ExerciseDefinitionResponse } from '../../constants/types';
import { API_ENDPOINTS } from '../../config/api';
import axios from 'axios';
import { format, formatDuration, intervalToDuration } from 'date-fns';
import ExerciseSelectionDialog from '../../components/workout/ExerciseSelectionDialog';
import SetsRepsExerciseDialog from '../../components/workout/SetsRepsExerciseDialog';
import DistanceExerciseDialog from '../../components/workout/DistanceExerciseDialog';
import SetsTimeExerciseDialog from '../../components/workout/SetsTimeExerciseDialog';
import { useUser } from '../../context/UserContext';
import { useNavigation } from '@react-navigation/native';

type Props = {
  navigation: any; // Replace with proper navigation type
};

type TrackedExercise = {
  exerciseDefinition: ExerciseDefinitionResponse;
  sets?: any[];
  distance?: number;
  distanceUnit?: DistanceUnit;
  startTime: Date;
  endTime?: Date;
};

const WorkoutTrackingScreen: React.FC<Props> = ({ navigation }) => {
  const { user } = useUser();
  const [workoutTypes, setWorkoutTypes] = useState<WorkoutTypeResponse[]>([]);
  const [selectedWorkoutType, setSelectedWorkoutType] = useState<WorkoutTypeResponse | null>(null);
  const [workoutStartTime, setWorkoutStartTime] = useState<Date | null>(null);
  const [duration, setDuration] = useState<string>('00:00:00');
  const [showExerciseSelection, setShowExerciseSelection] = useState(false);
  const [showExerciseTracking, setShowExerciseTracking] = useState(false);
  const [selectedExercise, setSelectedExercise] = useState<ExerciseDefinitionResponse | null>(null);
  const [trackedExercises, setTrackedExercises] = useState<TrackedExercise[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchWorkoutTypes = async () => {
      if (!user?.token) {
        setError('Authentication required');
        return;
      }

      try {
        const response = await axios.get(API_ENDPOINTS.WORKOUT_TYPES, {
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        });
        setWorkoutTypes(response.data);
      } catch (error) {
        console.error('Error fetching workout types:', error);
        setError('Failed to load workout types');
      }
    };
    fetchWorkoutTypes();
  }, [user]);

  useEffect(() => {
    let intervalId: NodeJS.Timeout;
    if (workoutStartTime) {
      intervalId = setInterval(() => {
        const duration = intervalToDuration({
          start: workoutStartTime,
          end: new Date(),
        });
        setDuration(
          formatDuration(duration, {
            format: ['hours', 'minutes', 'seconds'],
            zero: true,
            delimiter: ':',
          })
        );
      }, 1000);
    }
    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [workoutStartTime]);

  const handleWorkoutTypeSelect = (workoutType: WorkoutTypeResponse) => {
    setSelectedWorkoutType(workoutType);
    setWorkoutStartTime(new Date());
  };

  const handleExerciseSelect = (exercise: ExerciseDefinitionResponse) => {
    setSelectedExercise(exercise);
    setShowExerciseSelection(false);
    setShowExerciseTracking(true);
  };

  const handleSetsRepsExerciseSave = (sets: any[]) => {
    if (selectedExercise) {
      const trackedExercise: TrackedExercise = {
        exerciseDefinition: selectedExercise,
        sets,
        startTime: new Date(Math.min(...sets.map(s => s.startTime?.getTime() || Date.now()))),
        endTime: new Date(Math.max(...sets.map(s => s.endTime?.getTime() || Date.now()))),
      };
      setTrackedExercises([...trackedExercises, trackedExercise]);
      setShowExerciseTracking(false);
      setSelectedExercise(null);
    }
  };

  const handleDistanceExerciseSave = (data: { distance: number; unit: DistanceUnit }) => {
    if (selectedExercise) {
      const now = new Date();
      const trackedExercise: TrackedExercise = {
        exerciseDefinition: selectedExercise,
        distance: data.distance,
        distanceUnit: data.unit,
        startTime: now,
        endTime: now,
      };
      setTrackedExercises([...trackedExercises, trackedExercise]);
      setShowExerciseTracking(false);
      setSelectedExercise(null);
    }
  };

  const handleSetsTimeExerciseSave = (sets: any[]) => {
    if (selectedExercise) {
      const trackedExercise: TrackedExercise = {
        exerciseDefinition: selectedExercise,
        sets,
        startTime: new Date(Math.min(...sets.map(s => s.startTime?.getTime() || Date.now()))),
        endTime: new Date(Math.max(...sets.map(s => s.endTime?.getTime() || Date.now()))),
      };
      setTrackedExercises([...trackedExercises, trackedExercise]);
      setShowExerciseTracking(false);
      setSelectedExercise(null);
    }
  };

  const handleSaveWorkout = async () => {
    if (!selectedWorkoutType || !workoutStartTime || !user?.token) return;

    const workoutData = {
      workoutTypeId: selectedWorkoutType.id,
      startTime: workoutStartTime.toISOString(),
      endTime: new Date().toISOString(),
      exercises: trackedExercises.map(exercise => {
        const baseExercise = {
          exerciseDefinitionId: exercise.exerciseDefinition.id,
          startTime: exercise.startTime.toISOString(),
          endTime: exercise.endTime?.toISOString(),
        };

        switch (exercise.exerciseDefinition.type) {
          case ExerciseType.SETS_REPS:
          case ExerciseType.SETS_TIME:
            return {
              ...baseExercise,
              sets: exercise.sets,
            };
          case ExerciseType.DISTANCE:
            return {
              ...baseExercise,
              distance: exercise.distance,
              distanceUnit: exercise.distanceUnit,
            };
          default:
            return baseExercise;
        }
      }),
    };

    try {
      await axios.post(API_ENDPOINTS.WORKOUTS, workoutData, {
        headers: {
          Authorization: `Bearer ${user.token}`,
        },
      });
      navigation.goBack(); // Navigate back to workouts screen after successful save
    } catch (error) {
      console.error('Error saving workout:', error);
      setError('Failed to save workout');
    }
  };

  const getExerciseStats = (exercise: TrackedExercise) => {
    switch (exercise.exerciseDefinition.type) {
      case ExerciseType.SETS_REPS:
      case ExerciseType.SETS_TIME:
        return `${exercise.sets?.length} sets • ${format(exercise.startTime, 'HH:mm')}`;
      case ExerciseType.DISTANCE:
        return `${exercise.distance} ${exercise.distanceUnit?.toLowerCase()} • ${format(exercise.startTime, 'HH:mm')}`;
      default:
        return format(exercise.startTime, 'HH:mm');
    }
  };

  if (error) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.errorContainer}>
          <Text style={styles.errorText}>{error}</Text>
          <TouchableOpacity
            style={styles.retryButton}
            onPress={() => navigation.goBack()}
          >
            <Text style={styles.buttonText}>Go Back</Text>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        {!selectedWorkoutType ? (
          <View style={styles.typeSelectionContainer}>
            <Text style={styles.title}>Select Workout Type</Text>
            <View style={styles.typeGrid}>
              {workoutTypes.map((type) => (
                <TouchableOpacity
                  key={type.id}
                  style={styles.typeCard}
                  onPress={() => handleWorkoutTypeSelect(type)}
                >
                  <Text style={styles.typeText}>{type.name}</Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>
        ) : (
          <View style={styles.workoutContainer}>
            <View style={styles.header}>
              <Text style={styles.workoutTitle}>{selectedWorkoutType.name}</Text>
              <Text style={styles.timer}>{duration}</Text>
            </View>

            {trackedExercises.map((exercise, index) => (
              <View key={index} style={styles.exerciseCard}>
                <Text style={styles.exerciseName}>
                  {exercise.exerciseDefinition.name}
                </Text>
                <Text style={styles.exerciseStats}>
                  {getExerciseStats(exercise)}
                </Text>
              </View>
            ))}

            <TouchableOpacity
              style={styles.addExerciseButton}
              onPress={() => setShowExerciseSelection(true)}
            >
              <Text style={styles.addExerciseText}>Add Exercise</Text>
            </TouchableOpacity>

            {trackedExercises.length > 0 && (
              <TouchableOpacity
                style={styles.finishButton}
                onPress={handleSaveWorkout}
              >
                <Text style={styles.buttonText}>Finish Workout</Text>
              </TouchableOpacity>
            )}
          </View>
        )}
      </ScrollView>

      <ExerciseSelectionDialog
        visible={showExerciseSelection}
        onClose={() => setShowExerciseSelection(false)}
        onSelect={handleExerciseSelect}
        workoutTypeId={selectedWorkoutType?.id || 0}
      />

      {selectedExercise?.type === ExerciseType.SETS_REPS && (
        <SetsRepsExerciseDialog
          visible={showExerciseTracking}
          onClose={() => setShowExerciseTracking(false)}
          onSave={handleSetsRepsExerciseSave}
          exercise={selectedExercise}
        />
      )}

      {selectedExercise?.type === ExerciseType.DISTANCE && (
        <DistanceExerciseDialog
          visible={showExerciseTracking}
          onClose={() => setShowExerciseTracking(false)}
          onSave={handleDistanceExerciseSave}
          exercise={selectedExercise}
        />
      )}

      {selectedExercise?.type === ExerciseType.SETS_TIME && (
        <SetsTimeExerciseDialog
          visible={showExerciseTracking}
          onClose={() => setShowExerciseTracking(false)}
          onSave={handleSetsTimeExerciseSave}
          exercise={selectedExercise}
        />
      )}
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  scrollContent: {
    flexGrow: 1,
    padding: spacing.md,
  },
  typeSelectionContainer: {
    flex: 1,
    alignItems: 'center',
  },
  title: {
    fontSize: typography.sizes.xl,
    fontWeight: typography.weights.bold,
    color: colors.text,
    marginBottom: spacing.lg,
  },
  typeGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    gap: spacing.md,
  },
  typeCard: {
    backgroundColor: colors.surface,
    padding: spacing.lg,
    borderRadius: borderRadius.lg,
    minWidth: 150,
    alignItems: 'center',
    ...shadows.sm,
  },
  typeText: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
    color: colors.text,
  },
  workoutContainer: {
    flex: 1,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.lg,
  },
  workoutTitle: {
    fontSize: typography.sizes.xl,
    fontWeight: typography.weights.bold,
    color: colors.text,
  },
  timer: {
    fontSize: typography.sizes.lg,
    fontWeight: typography.weights.semibold,
    color: colors.primary,
  },
  exerciseCard: {
    backgroundColor: colors.surface,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    marginBottom: spacing.md,
    ...shadows.sm,
  },
  exerciseName: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.semibold,
    color: colors.text,
    marginBottom: spacing.xs,
  },
  exerciseStats: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
  },
  addExerciseButton: {
    backgroundColor: colors.primary,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    alignItems: 'center',
    marginBottom: spacing.md,
  },
  finishButton: {
    backgroundColor: colors.success,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    alignItems: 'center',
  },
  addExerciseText: {
    color: colors.background,
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.semibold,
  },
  buttonText: {
    color: colors.background,
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.semibold,
  },
  errorContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: spacing.lg,
  },
  errorText: {
    color: colors.error,
    fontSize: typography.sizes.md,
    textAlign: 'center',
    marginBottom: spacing.md,
  },
  retryButton: {
    backgroundColor: colors.primary,
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.sm,
    borderRadius: borderRadius.md,
  },
});

export default WorkoutTrackingScreen; 