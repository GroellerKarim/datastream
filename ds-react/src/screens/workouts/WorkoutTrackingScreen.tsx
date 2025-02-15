import React, { useEffect, useState, useCallback } from 'react';
import { View, Text, StyleSheet, ScrollView, ActivityIndicator } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { colors, typography, spacing, borderRadius, shadows } from '../../constants/theme';
import { Button } from '../../components/common/Button';
import { useUser } from '../../context/UserContext';
import axios from 'axios';
import { API_BASE_URL } from '../../config/api';
import { ExerciseType, ExerciseRecordResponse, ExerciseRecordDetailsResponse, ExerciseSetResponse } from '../../constants/types';
import { ExerciseSelector } from '../../components/exercise/ExerciseSelector';
import { SetsRepsExercise } from '../../components/exercise/SetsRepsExercise';
import { DistanceExercise } from '../../components/exercise/DistanceExercise';
import { SetsTimeExercise } from '../../components/exercise/SetsTimeExercise';

type WorkoutTypeResponse = {
  id: number;
  name: string;
};

type ExerciseDefinition = {
  id: number;
  name: string;
  type: ExerciseType;
};

type Props = {
  navigation: any; // TODO: Replace with proper navigation type
};

export const WorkoutTrackingScreen: React.FC<Props> = ({ navigation }) => {
  const insets = useSafeAreaInsets();
  const { user } = useUser();
  const [isLoading, setIsLoading] = useState(true);
  const [workoutTypes, setWorkoutTypes] = useState<WorkoutTypeResponse[]>([]);
  const [selectedWorkoutType, setSelectedWorkoutType] = useState<WorkoutTypeResponse | null>(null);
  const [workoutStartTime, setWorkoutStartTime] = useState<Date | null>(null);
  const [currentDuration, setCurrentDuration] = useState<number>(0);
  const [error, setError] = useState<string | null>(null);
  const [exercises, setExercises] = useState<ExerciseRecordResponse[]>([]);
  const [currentExercise, setCurrentExercise] = useState<ExerciseDefinition | null>(null);

  useEffect(() => {
    fetchWorkoutTypes();
  }, []);

  useEffect(() => {
    let intervalId: NodeJS.Timeout;
    if (workoutStartTime) {
      intervalId = setInterval(() => {
        setCurrentDuration(Date.now() - workoutStartTime.getTime());
      }, 1000);
    }
    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [workoutStartTime]);

  const fetchWorkoutTypes = async () => {
    if (!user?.token) {
      setError('Authentication required');
      setIsLoading(false);
      return;
    }

    try {
      setIsLoading(true);
      setError(null);
      const response = await axios.get<WorkoutTypeResponse[]>(
        `${API_BASE_URL}/api/v1/workouts/types`,
        {
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        }
      );
      setWorkoutTypes(response.data);
    } catch (err) {
      setError('Failed to load workout types');
      console.error('Error fetching workout types:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleWorkoutTypeSelect = (workoutType: WorkoutTypeResponse) => {
    setSelectedWorkoutType(workoutType);
    setWorkoutStartTime(new Date());
  };

  const handleExerciseSelect = (exercise: ExerciseDefinition) => {
    setCurrentExercise(exercise);
  };

  const handleExerciseComplete = (details: ExerciseRecordDetailsResponse) => {
    if (!currentExercise) return;

    const exerciseRecord: ExerciseRecordResponse = {
      exerciseRecordId: Math.random(), // This will be replaced by the backend
      exerciseDefinitionId: currentExercise.id,
      exerciseName: currentExercise.name,
      type: currentExercise.type,
      startTime: new Date().toISOString(),
      endTime: new Date().toISOString(),
      details,
      orderIndex: exercises.length,
    };

    setExercises((prev) => [...prev, exerciseRecord]);
    setCurrentExercise(null);
  };

  const handleWorkoutComplete = async () => {
    if (!selectedWorkoutType || !workoutStartTime || !user?.token) return;

    try {
      setIsLoading(true);
      await axios.post(
        `${API_BASE_URL}/api/v1/workouts`,
        {
          workoutTypeId: selectedWorkoutType.id,
          startTime: workoutStartTime.toISOString(),
          endTime: new Date().toISOString(),
          exercises: exercises.map(({ exerciseRecordId, ...exercise }) => exercise),
        },
        {
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        }
      );
      navigation.goBack();
    } catch (err) {
      setError('Failed to save workout');
      console.error('Error saving workout:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const formatDuration = (durationMs: number): string => {
    const hours = Math.floor(durationMs / (1000 * 60 * 60));
    const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((durationMs % (1000 * 60)) / 1000);
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  };

  const renderExerciseComponent = () => {
    if (!currentExercise) return null;

    const commonProps = {
      exerciseId: currentExercise.id,
      exerciseName: currentExercise.name,
    };

    switch (currentExercise.type) {
      case ExerciseType.SETS_REPS:
        return (
          <SetsRepsExercise
            {...commonProps}
            onComplete={(sets) => handleExerciseComplete({ sets })}
          />
        );
      case ExerciseType.DISTANCE:
        return (
          <DistanceExercise
            {...commonProps}
            onComplete={handleExerciseComplete}
          />
        );
      case ExerciseType.SETS_TIME:
        return (
          <SetsTimeExercise
            {...commonProps}
            onComplete={(sets) => handleExerciseComplete({ sets })}
          />
        );
      default:
        return null;
    }
  };

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={styles.header}>
        <Button
          variant="outline"
          title="Back"
          onPress={() => navigation.goBack()}
          style={styles.backButton}
        />
        <Text style={styles.title}>Track Workout</Text>
      </View>

      {workoutStartTime && (
        <View style={styles.durationContainer}>
          <Text style={styles.durationLabel}>Duration</Text>
          <Text style={styles.durationValue}>{formatDuration(currentDuration)}</Text>
        </View>
      )}

      <ScrollView style={styles.content}>
        {isLoading ? (
          <View style={styles.loadingContainer}>
            <ActivityIndicator size="large" color={colors.primary} />
          </View>
        ) : error ? (
          <View style={styles.errorContainer}>
            <Text style={styles.errorText}>{error}</Text>
            <Button
              title="Retry"
              onPress={fetchWorkoutTypes}
              style={styles.retryButton}
            />
          </View>
        ) : !selectedWorkoutType ? (
          <View style={styles.workoutTypeContainer}>
            <Text style={styles.sectionTitle}>Select Workout Type</Text>
            <View style={styles.workoutTypeGrid}>
              {workoutTypes.map((type) => (
                <Button
                  key={type.id}
                  title={type.name}
                  onPress={() => handleWorkoutTypeSelect(type)}
                  style={styles.workoutTypeButton}
                />
              ))}
            </View>
          </View>
        ) : (
          <View style={styles.exerciseContainer}>
            {currentExercise ? (
              renderExerciseComponent()
            ) : (
              <>
                <View style={styles.exerciseHeader}>
                  <Text style={styles.sectionTitle}>Exercises</Text>
                  <ExerciseSelector
                    onSelect={handleExerciseSelect}
                    workoutTypeId={selectedWorkoutType.id}
                  />
                </View>

                {exercises.map((exercise, index) => (
                  <View key={exercise.exerciseRecordId} style={styles.exerciseCard}>
                    <View style={styles.exerciseCardHeader}>
                      <Text style={styles.exerciseName}>{exercise.exerciseName}</Text>
                      <Text style={styles.exerciseType}>{exercise.type}</Text>
                    </View>

                    {exercise.type === ExerciseType.SETS_REPS && exercise.details.sets && (
                      <Text style={styles.exerciseDetails}>
                        {exercise.details.sets.length} sets • {exercise.details.sets.reduce((total, set) => total + set.repetitions, 0)} total reps
                      </Text>
                    )}

                    {exercise.type === ExerciseType.DISTANCE && exercise.details.distance && (
                      <Text style={styles.exerciseDetails}>
                        {exercise.details.distance} {exercise.details.distanceUnit?.toLowerCase()}
                      </Text>
                    )}

                    {exercise.type === ExerciseType.SETS_TIME && exercise.details.sets && (
                      <Text style={styles.exerciseDetails}>
                        {exercise.details.sets.length} sets • {formatDuration(exercise.details.sets.reduce((total, set) => 
                          total + (new Date(set.endTime).getTime() - new Date(set.startTime).getTime()), 0
                        ))} total time
                      </Text>
                    )}
                  </View>
                ))}

                {exercises.length > 0 && (
                  <Button
                    title="Complete Workout"
                    onPress={handleWorkoutComplete}
                    style={styles.completeButton}
                  />
                )}
              </>
            )}
          </View>
        )}
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: spacing.md,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
  },
  backButton: {
    marginRight: spacing.md,
  },
  title: {
    flex: 1,
    fontSize: typography.sizes.xl,
    fontWeight: typography.weights.semibold,
    color: colors.text,
  },
  content: {
    flex: 1,
  },
  durationContainer: {
    padding: spacing.md,
    backgroundColor: colors.surface,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
    alignItems: 'center',
  },
  durationLabel: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
    marginBottom: spacing.xs,
  },
  durationValue: {
    fontSize: typography.sizes.xxl,
    fontWeight: typography.weights.bold,
    color: colors.primary,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: spacing.xl,
  },
  errorContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: spacing.xl,
  },
  errorText: {
    color: colors.error,
    fontSize: typography.sizes.md,
    textAlign: 'center',
    marginBottom: spacing.md,
  },
  retryButton: {
    minWidth: 120,
  },
  workoutTypeContainer: {
    padding: spacing.md,
  },
  sectionTitle: {
    fontSize: typography.sizes.lg,
    fontWeight: typography.weights.semibold,
    color: colors.text,
    marginBottom: spacing.md,
  },
  workoutTypeGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: spacing.md,
  },
  workoutTypeButton: {
    minWidth: 150,
  },
  exerciseContainer: {
    padding: spacing.md,
  },
  exerciseHeader: {
    marginBottom: spacing.lg,
  },
  exerciseCard: {
    backgroundColor: colors.surface,
    borderRadius: borderRadius.md,
    padding: spacing.md,
    marginBottom: spacing.md,
    ...shadows.sm,
  },
  exerciseCardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.xs,
  },
  exerciseName: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
    color: colors.text,
  },
  exerciseType: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
  },
  exerciseDetails: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
  },
  completeButton: {
    marginTop: spacing.lg,
  },
}); 