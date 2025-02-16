import React, { useEffect, useState } from 'react';
import { View, Text, Pressable, StyleSheet, ScrollView, ActivityIndicator } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import Icon from '@expo/vector-icons/Feather';
import { colors, typography, spacing, borderRadius, shadows } from '../../constants/theme';
import { WorkoutResponse, WorkoutListResponse } from '../../constants/types';
import axios from 'axios';
import { API_BASE_URL } from '../../config/api';
import { useUser } from '../../context/UserContext';

type Props = {
  navigation: any; // Replace with proper navigation type
};

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
};

const formatDuration = (durationMs: number): string => {
  const minutes = Math.round(durationMs / (1000 * 60));
  return `${minutes} min`;
};

export const WorkoutsScreen: React.FC<Props> = ({ navigation }) => {
  const insets = useSafeAreaInsets();
  const { user } = useUser();
  const [isLoading, setIsLoading] = useState(true);
  const [workouts, setWorkouts] = useState<WorkoutResponse[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchWorkouts();
  }, [user]);

  const fetchWorkouts = async () => {
    if (!user?.token) {
      setError('Authentication required');
      setIsLoading(false);
      return;
    }

    try {
      setIsLoading(true);
      setError(null);
      const response = await axios.get<WorkoutListResponse>(
        `${API_BASE_URL}/api/v1/workouts?size=5&sort=startTime,desc`,
        {
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        }
      );
      setWorkouts(response.data.content);
    } catch (err) {
      setError('Failed to load workouts');
      console.error('Error fetching workouts:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleWorkoutPress = (workout: WorkoutResponse) => {
    navigation.navigate('WorkoutDetails', { 
      workoutId: workout.workoutId,
      workout // Pass the entire workout data
    });
  };

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={styles.header}>
        <Pressable 
          onPress={() => navigation.goBack()}
          style={styles.backButton}
        >
          <Icon name="arrow-left" size={24} color={colors.text} />
        </Pressable>
        <Text style={styles.title}>Workouts & Exercises</Text>
      </View>

      <View style={styles.actionButtons}>
        <Pressable 
          style={[styles.actionButton, styles.primaryButton]}
          onPress={() => navigation.navigate('NewWorkout')}
        >
          <Icon name="plus-circle" size={20} color={colors.background} />
          <Text style={styles.primaryButtonText}>Start Workout</Text>
        </Pressable>

        <Pressable 
          style={[styles.actionButton, styles.secondaryButton]}
          onPress={() => navigation.navigate('WorkoutStats')}
        >
          <Icon name="bar-chart-2" size={20} color={colors.primary} />
          <Text style={styles.secondaryButtonText}>View Progress</Text>
        </Pressable>
      </View>

      <View style={styles.content}>
        <Text style={styles.sectionTitle}>Recent Workouts</Text>
        {isLoading ? (
          <View style={styles.loadingContainer}>
            <ActivityIndicator size="large" color={colors.primary} />
          </View>
        ) : error ? (
          <View style={styles.errorContainer}>
            <Text style={styles.errorText}>{error}</Text>
            <Pressable 
              style={styles.retryButton}
              onPress={fetchWorkouts}
            >
              <Text style={styles.retryButtonText}>Retry</Text>
            </Pressable>
          </View>
        ) : (
          <ScrollView style={styles.workoutsList}>
            {workouts.map((workout) => (
              <Pressable
                key={workout.workoutId}
                style={styles.workoutCard}
                onPress={() => handleWorkoutPress(workout)}
              >
                <View style={styles.workoutInfo}>
                  <Text style={styles.workoutName}>{workout.workoutType}</Text>
                  <Text style={styles.workoutMeta}>
                    {formatDate(workout.date)} • {formatDuration(workout.durationMs)}
                  </Text>
                </View>
                <Icon name="chevron-right" size={20} color={colors.textSecondary} />
              </Pressable>
            ))}
          </ScrollView>
        )}
      </View>
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
    padding: spacing.sm,
    marginRight: spacing.sm,
  },
  title: {
    fontSize: typography.sizes.xl,
    fontWeight: typography.weights.semibold,
    color: colors.text,
  },
  actionButtons: {
    flexDirection: 'row',
    padding: spacing.md,
    gap: spacing.md,
  },
  actionButton: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    padding: spacing.md,
    borderRadius: borderRadius.md,
    gap: spacing.sm,
    ...shadows.sm,
  },
  primaryButton: {
    backgroundColor: colors.primary,
  },
  secondaryButton: {
    backgroundColor: colors.background,
    borderWidth: 1,
    borderColor: colors.primary,
  },
  primaryButtonText: {
    color: colors.background,
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
  },
  secondaryButtonText: {
    color: colors.primary,
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
  },
  content: {
    flex: 1,
    padding: spacing.md,
  },
  sectionTitle: {
    fontSize: typography.sizes.lg,
    fontWeight: typography.weights.semibold,
    color: colors.text,
    marginBottom: spacing.md,
  },
  workoutsList: {
    flex: 1,
  },
  workoutCard: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: spacing.md,
    backgroundColor: colors.cardBg,
    borderRadius: borderRadius.md,
    marginBottom: spacing.sm,
    ...shadows.sm,
  },
  workoutInfo: {
    flex: 1,
  },
  workoutName: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
    color: colors.text,
    marginBottom: spacing.xs,
  },
  workoutMeta: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
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
  retryButtonText: {
    color: colors.background,
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
  },
}); 