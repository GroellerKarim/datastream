import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  TextInput,
  ScrollView,
  Switch,
} from 'react-native';
import { colors, spacing, typography, borderRadius } from '../../constants/theme';
import { ExerciseDefinitionResponse } from '../../constants/types';
import { format, formatDuration, intervalToDuration } from 'date-fns';

type ExerciseSet = {
  startTime: Date | null;
  endTime: Date | null;
  reps: number;
  weight: number;
  failure: boolean;
  partialReps?: number;
};

type Props = {
  visible: boolean;
  onClose: () => void;
  onSave: (sets: ExerciseSet[]) => void;
  exercise: ExerciseDefinitionResponse;
};

const SetsRepsExerciseDialog: React.FC<Props> = ({
  visible,
  onClose,
  onSave,
  exercise,
}) => {
  const [sets, setSets] = useState<ExerciseSet[]>([]);
  const [currentSet, setCurrentSet] = useState<number | null>(null);
  const [restTimer, setRestTimer] = useState<string>('00:00');
  const [isSetInProgress, setIsSetInProgress] = useState(false);

  useEffect(() => {
    if (visible) {
      setSets([{
        startTime: null,
        endTime: null,
        reps: 0,
        weight: 0,
        failure: false,
        partialReps: 0,
      }]);
      setCurrentSet(null);
      setIsSetInProgress(false);
      setRestTimer('00:00');
    }
  }, [visible]);

  useEffect(() => {
    let intervalId: NodeJS.Timeout;
    if (currentSet !== null && sets[currentSet].endTime) {
      intervalId = setInterval(() => {
        const duration = intervalToDuration({
          start: sets[currentSet].endTime!,
          end: new Date(),
        });
        setRestTimer(
          formatDuration(duration, {
            format: ['minutes', 'seconds'],
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
  }, [currentSet, sets]);

  const addSet = () => {
    setSets([
      ...sets,
      {
        startTime: null,
        endTime: null,
        reps: 0,
        weight: 0,
        failure: false,
        partialReps: 0,
      },
    ]);
  };

  const startSet = (index: number) => {
    setIsSetInProgress(true);
    const updatedSets = [...sets];
    updatedSets[index] = {
      ...updatedSets[index],
      startTime: new Date(),
    };
    setSets(updatedSets);
    setCurrentSet(index);
  };

  const endSet = (index: number) => {
    setIsSetInProgress(false);
    const updatedSets = [...sets];
    updatedSets[index] = {
      ...updatedSets[index],
      endTime: new Date(),
    };
    setSets(updatedSets);
  };

  const updateSet = (index: number, updates: Partial<ExerciseSet>) => {
    const updatedSets = [...sets];
    updatedSets[index] = {
      ...updatedSets[index],
      ...updates,
    };
    setSets(updatedSets);
  };

  const completeSet = (index: number) => {
    if (index === sets.length - 1) {
      // Add a new set automatically when completing the last set
      addSet();
    }
    setCurrentSet(null);
  };

  const getSetDuration = (set: ExerciseSet) => {
    if (!set.startTime || !set.endTime) return '00:00';
    const durationInMs = set.endTime.getTime() - set.startTime.getTime();
    const seconds = Math.floor(durationInMs / 1000);
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  return (
    <Modal visible={visible} animationType="slide" transparent>
      <View style={styles.modalContainer}>
        <View style={styles.content}>
          <View style={styles.header}>
            <Text style={styles.title}>{exercise.name}</Text>
            <TouchableOpacity onPress={onClose} style={styles.closeButton}>
              <Text style={styles.closeText}>✕</Text>
            </TouchableOpacity>
          </View>

          <View style={styles.mainContent}>
            {currentSet !== null && sets[currentSet].endTime && (
              <View style={styles.restTimer}>
                <Text style={styles.restTimerLabel}>Rest Time:</Text>
                <Text style={styles.restTimerValue}>{restTimer}</Text>
              </View>
            )}

            <ScrollView style={styles.setsList} contentContainerStyle={styles.setsListContent}>
              {sets.map((set, index) => (
                <View key={index} style={styles.setItem}>
                  <View style={styles.setHeader}>
                    <Text style={styles.setTitle}>Set {index + 1}</Text>
                    {set.startTime && set.endTime && (
                      <View style={styles.durationContainer}>
                        <Text style={styles.durationLabel}>Duration:</Text>
                        <Text style={styles.durationValue}>{getSetDuration(set)}</Text>
                      </View>
                    )}
                  </View>

                  <View style={styles.setControls}>
                    {!set.startTime ? (
                      <TouchableOpacity
                        style={[styles.startButton, isSetInProgress && styles.disabledButton]}
                        onPress={() => startSet(index)}
                        disabled={isSetInProgress}
                      >
                        <Text style={[styles.buttonText, isSetInProgress && styles.disabledButtonText]}>Start Set</Text>
                      </TouchableOpacity>
                    ) : !set.endTime ? (
                      <TouchableOpacity
                        style={styles.endButton}
                        onPress={() => endSet(index)}
                      >
                        <Text style={styles.buttonText}>Stop Set</Text>
                      </TouchableOpacity>
                    ) : (
                      <View style={styles.setInputs}>
                        <View style={styles.inputsContainer}>
                          <View style={styles.column}>
                            <View style={styles.inputGroup}>
                              <Text style={styles.inputLabel}>Weight (kg)</Text>
                              <TextInput
                                style={styles.input}
                                keyboardType="numeric"
                                value={set.weight.toString()}
                                onChangeText={(value) =>
                                  updateSet(index, { weight: Number(value) || 0 })
                                }
                              />
                            </View>
                            <View style={styles.inputGroup}>
                              <Text style={styles.inputLabel}>Partial Reps</Text>
                              <TextInput
                                style={[styles.input, !set.failure && styles.disabledInput]}
                                keyboardType="numeric"
                                value={set.partialReps?.toString() || '0'}
                                onChangeText={(value) =>
                                  updateSet(index, { partialReps: Number(value) || 0 })
                                }
                                editable={set.failure}
                              />
                            </View>
                          </View>
                          <View style={styles.column}>
                            <View style={styles.inputGroup}>
                              <Text style={styles.inputLabel}>Reps</Text>
                              <TextInput
                                style={styles.input}
                                keyboardType="numeric"
                                value={set.reps.toString()}
                                onChangeText={(value) =>
                                  updateSet(index, { reps: Number(value) || 0 })
                                }
                              />
                            </View>
                            <View style={[styles.inputGroup, styles.failureGroup]}>
                              <Text style={styles.inputLabel}>Failure</Text>
                              <Switch
                                value={set.failure}
                                onValueChange={(value) =>
                                  updateSet(index, { failure: value })
                                }
                              />
                            </View>
                          </View>
                        </View>
                        <View style={styles.completeSetContainer}>
                          <TouchableOpacity
                            style={styles.completeSetButton}
                            onPress={() => completeSet(index)}
                          >
                            <Text style={styles.buttonText}>Complete Set</Text>
                          </TouchableOpacity>
                        </View>
                      </View>
                    )}
                  </View>
                </View>
              ))}
            </ScrollView>
          </View>

          <View style={styles.footer}>
            <TouchableOpacity
              style={styles.completeExerciseButton}
              onPress={() => onSave(sets)}
              disabled={sets.length === 0 || sets.some(set => !set.endTime)}
            >
              <Text style={styles.buttonText}>Complete Exercise</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  modalContainer: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'flex-end',
  },
  content: {
    backgroundColor: colors.background,
    borderTopLeftRadius: borderRadius.xl,
    borderTopRightRadius: borderRadius.xl,
    height: '90%',
    overflow: 'hidden',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: spacing.lg,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
  },
  mainContent: {
    flex: 1,
    paddingHorizontal: spacing.lg,
  },
  title: {
    fontSize: typography.sizes.xl,
    fontWeight: typography.weights.bold,
    color: colors.text,
  },
  closeButton: {
    padding: spacing.sm,
  },
  closeText: {
    fontSize: typography.sizes.xl,
    color: colors.textSecondary,
  },
  restTimer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.surface,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    marginBottom: spacing.md,
  },
  restTimerLabel: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
    color: colors.text,
    marginRight: spacing.sm,
  },
  restTimerValue: {
    fontSize: typography.sizes.lg,
    fontWeight: typography.weights.bold,
    color: colors.primary,
  },
  setsList: {
    flex: 1,
  },
  setsListContent: {
    paddingVertical: spacing.md,
  },
  setItem: {
    backgroundColor: colors.surface,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    marginBottom: spacing.md,
  },
  setHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.sm,
  },
  setTitle: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.semibold,
    color: colors.text,
  },
  durationContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.surfaceHover,
    paddingHorizontal: spacing.sm,
    paddingVertical: spacing.xs,
    borderRadius: borderRadius.sm,
  },
  durationLabel: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
    marginRight: spacing.xs,
  },
  durationValue: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.semibold,
    color: colors.text,
  },
  setControls: {
    gap: spacing.sm,
  },
  startButton: {
    backgroundColor: colors.success,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    alignItems: 'center',
  },
  endButton: {
    backgroundColor: colors.error,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    alignItems: 'center',
  },
  buttonText: {
    color: colors.background,
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.semibold,
  },
  setInputs: {
    gap: spacing.md,
  },
  inputsContainer: {
    flexDirection: 'row',
    gap: spacing.md,
  },
  column: {
    flex: 1,
    flexDirection: 'column',
    gap: spacing.md,
  },
  inputGroup: {
    flex: 1,
    minWidth: 100,
  },
  failureGroup: {
    alignItems: 'center',
    justifyContent: 'flex-end',
    paddingBottom: spacing.sm,
  },
  inputLabel: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
    marginBottom: spacing.xs,
  },
  input: {
    backgroundColor: colors.background,
    padding: spacing.sm,
    borderRadius: borderRadius.md,
    fontSize: typography.sizes.md,
    textAlign: 'center',
  },
  footer: {
    padding: spacing.lg,
    borderTopWidth: 1,
    borderTopColor: colors.border,
    backgroundColor: colors.background,
  },
  completeSetContainer: {
    alignItems: 'center',
    width: '100%',
  },
  completeSetButton: {
    backgroundColor: colors.success,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    width: '80%',
    alignItems: 'center',
  },
  completeExerciseButton: {
    backgroundColor: colors.primary,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    marginTop: spacing.lg,
  },
  disabledButton: {
    backgroundColor: colors.surfaceHover,
  },
  disabledButtonText: {
    color: colors.textSecondary,
  },
  disabledInput: {
    backgroundColor: colors.surfaceHover,
    opacity: 0.7,
  },
});

export default SetsRepsExerciseDialog; 