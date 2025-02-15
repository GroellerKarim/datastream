import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { colors, typography, spacing, borderRadius, shadows } from '../../constants/theme';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { ExerciseSetResponse } from '../../constants/types';

type Props = {
  exerciseId: number;
  exerciseName: string;
  onComplete: (sets: ExerciseSetResponse[]) => void;
};

type SetData = ExerciseSetResponse & {
  isActive: boolean;
  duration: number;
  restTime: number;
};

export const SetsRepsExercise: React.FC<Props> = ({
  exerciseId,
  exerciseName,
  onComplete,
}) => {
  const [sets, setSets] = useState<SetData[]>([]);
  const [currentSet, setCurrentSet] = useState<number | null>(null);

  useEffect(() => {
    let intervalId: NodeJS.Timeout;
    if (currentSet !== null) {
      intervalId = setInterval(() => {
        setSets((prevSets) => {
          const newSets = [...prevSets];
          const set = newSets[currentSet];
          if (set.isActive) {
            set.duration = Date.now() - new Date(set.startTime).getTime();
          } else if (currentSet > 0) {
            set.restTime = Date.now() - new Date(prevSets[currentSet - 1].endTime).getTime();
          }
          return newSets;
        });
      }, 1000);
    }
    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [currentSet]);

  const startNewSet = () => {
    const newSet: SetData = {
      startTime: new Date().toISOString(),
      endTime: '',
      failure: false,
      repetitions: 0,
      weightKg: 0,
      isActive: true,
      duration: 0,
      restTime: 0,
    };
    setSets((prevSets) => [...prevSets, newSet]);
    setCurrentSet(sets.length);
  };

  const endCurrentSet = () => {
    if (currentSet === null) return;

    setSets((prevSets) => {
      const newSets = [...prevSets];
      newSets[currentSet] = {
        ...newSets[currentSet],
        endTime: new Date().toISOString(),
        isActive: false,
      };
      return newSets;
    });
  };

  const updateSetData = (index: number, data: Partial<SetData>) => {
    setSets((prevSets) => {
      const newSets = [...prevSets];
      newSets[index] = { ...newSets[index], ...data };
      return newSets;
    });
  };

  const formatDuration = (ms: number): string => {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  const handleComplete = () => {
    onComplete(sets.map(({ isActive, duration, restTime, ...set }) => set));
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>{exerciseName}</Text>
      
      <ScrollView style={styles.setsContainer}>
        {sets.map((set, index) => (
          <View key={index} style={styles.setCard}>
            <View style={styles.setHeader}>
              <Text style={styles.setTitle}>Set {index + 1}</Text>
              {set.isActive ? (
                <Text style={styles.duration}>Duration: {formatDuration(set.duration)}</Text>
              ) : index > 0 && (
                <Text style={styles.restTime}>Rest: {formatDuration(set.restTime)}</Text>
              )}
            </View>

            {set.isActive ? (
              <Button
                title="End Set"
                onPress={endCurrentSet}
                style={styles.setButton}
              />
            ) : !set.endTime ? (
              <View style={styles.setInputs}>
                <Input
                  label="Weight (kg)"
                  keyboardType="numeric"
                  value={set.weightKg.toString()}
                  onChangeText={(value) => updateSetData(index, { weightKg: parseFloat(value) || 0 })}
                  style={styles.input}
                />
                <Input
                  label="Reps"
                  keyboardType="numeric"
                  value={set.repetitions.toString()}
                  onChangeText={(value) => updateSetData(index, { repetitions: parseInt(value) || 0 })}
                  style={styles.input}
                />
                <View style={styles.failureContainer}>
                  <Text style={styles.failureLabel}>Failure?</Text>
                  <Button
                    title={set.failure ? 'Yes' : 'No'}
                    variant={set.failure ? 'primary' : 'outline'}
                    onPress={() => updateSetData(index, { failure: !set.failure })}
                    style={styles.failureButton}
                  />
                </View>
              </View>
            ) : (
              <View style={styles.setData}>
                <Text style={styles.dataText}>{set.weightKg} kg × {set.repetitions} reps</Text>
                {set.failure && <Text style={styles.failureText}>Failure</Text>}
              </View>
            )}
          </View>
        ))}
      </ScrollView>

      <View style={styles.footer}>
        {currentSet === null || !sets[currentSet]?.isActive ? (
          <Button
            title="Start New Set"
            onPress={startNewSet}
            style={styles.footerButton}
          />
        ) : null}
        {sets.length > 0 && sets.every((set) => !set.isActive && set.endTime) && (
          <Button
            title="Complete Exercise"
            onPress={handleComplete}
            style={styles.footerButton}
          />
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
  title: {
    fontSize: typography.sizes.xl,
    fontWeight: typography.weights.semibold,
    color: colors.text,
    padding: spacing.md,
  },
  setsContainer: {
    flex: 1,
    padding: spacing.md,
  },
  setCard: {
    backgroundColor: colors.surface,
    borderRadius: borderRadius.md,
    padding: spacing.md,
    marginBottom: spacing.md,
    ...shadows.sm,
  },
  setHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.md,
  },
  setTitle: {
    fontSize: typography.sizes.lg,
    fontWeight: typography.weights.medium,
    color: colors.text,
  },
  duration: {
    fontSize: typography.sizes.md,
    color: colors.primary,
  },
  restTime: {
    fontSize: typography.sizes.md,
    color: colors.textSecondary,
  },
  setButton: {
    marginTop: spacing.sm,
  },
  setInputs: {
    gap: spacing.md,
  },
  input: {
    marginBottom: 0,
  },
  failureContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  failureLabel: {
    fontSize: typography.sizes.md,
    color: colors.text,
  },
  failureButton: {
    minWidth: 80,
  },
  setData: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  dataText: {
    fontSize: typography.sizes.md,
    color: colors.text,
  },
  failureText: {
    fontSize: typography.sizes.sm,
    color: colors.error,
  },
  footer: {
    padding: spacing.md,
    borderTopWidth: 1,
    borderTopColor: colors.border,
    gap: spacing.md,
  },
  footerButton: {
    marginBottom: spacing.sm,
  },
}); 