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
import { ExerciseDefinitionResponse } from '../../types/responses';
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
      },
    ]);
  };

  const startSet = (index: number) => {
    const updatedSets = [...sets];
    updatedSets[index] = {
      ...updatedSets[index],
      startTime: new Date(),
    };
    setSets(updatedSets);
    setCurrentSet(index);
  };

  const endSet = (index: number) => {
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

  const getSetDuration = (set: ExerciseSet) => {
    if (!set.startTime || !set.endTime) return '00:00';
    const duration = intervalToDuration({
      start: set.startTime,
      end: set.endTime,
    });
    return formatDuration(duration, {
      format: ['minutes', 'seconds'],
      zero: true,
      delimiter: ':',
    });
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

          {currentSet !== null && sets[currentSet].endTime && (
            <View style={styles.restTimer}>
              <Text style={styles.restTimerLabel}>Rest Time:</Text>
              <Text style={styles.restTimerValue}>{restTimer}</Text>
            </View>
          )}

          <ScrollView style={styles.setsList}>
            {sets.map((set, index) => (
              <View key={index} style={styles.setItem}>
                <View style={styles.setHeader}>
                  <Text style={styles.setTitle}>Set {index + 1}</Text>
                  {set.startTime && set.endTime && (
                    <Text style={styles.setDuration}>{getSetDuration(set)}</Text>
                  )}
                </View>

                <View style={styles.setControls}>
                  {!set.startTime ? (
                    <TouchableOpacity
                      style={styles.startButton}
                      onPress={() => startSet(index)}
                    >
                      <Text style={styles.buttonText}>Start Set</Text>
                    </TouchableOpacity>
                  ) : !set.endTime ? (
                    <TouchableOpacity
                      style={styles.endButton}
                      onPress={() => endSet(index)}
                    >
                      <Text style={styles.buttonText}>End Set</Text>
                    </TouchableOpacity>
                  ) : (
                    <View style={styles.setInputs}>
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
                      <View style={styles.failureGroup}>
                        <Text style={styles.inputLabel}>Failure</Text>
                        <Switch
                          value={set.failure}
                          onValueChange={(value) =>
                            updateSet(index, { failure: value })
                          }
                        />
                      </View>
                      {set.failure && (
                        <View style={styles.inputGroup}>
                          <Text style={styles.inputLabel}>Partial Reps</Text>
                          <TextInput
                            style={styles.input}
                            keyboardType="numeric"
                            value={set.partialReps?.toString() || '0'}
                            onChangeText={(value) =>
                              updateSet(index, { partialReps: Number(value) || 0 })
                            }
                          />
                        </View>
                      )}
                    </View>
                  )}
                </View>
              </View>
            ))}
          </ScrollView>

          <View style={styles.footer}>
            <TouchableOpacity style={styles.addSetButton} onPress={addSet}>
              <Text style={styles.buttonText}>Add Set</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.saveButton}
              onPress={() => onSave(sets)}
            >
              <Text style={styles.buttonText}>Save Exercise</Text>
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
    padding: spacing.lg,
    maxHeight: '90%',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.lg,
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
  setDuration: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
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
    flexDirection: 'row',
    flexWrap: 'wrap',
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
    flexDirection: 'row',
    gap: spacing.md,
    marginTop: spacing.md,
  },
  addSetButton: {
    flex: 1,
    backgroundColor: colors.secondary,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    alignItems: 'center',
  },
  saveButton: {
    flex: 1,
    backgroundColor: colors.primary,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    alignItems: 'center',
  },
});

export default SetsRepsExerciseDialog; 