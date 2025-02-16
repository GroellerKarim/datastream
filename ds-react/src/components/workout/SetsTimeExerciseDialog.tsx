import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  ScrollView,
  Switch,
} from 'react-native';
import { colors, spacing, typography, borderRadius } from '../../constants/theme';
import { ExerciseDefinitionResponse } from '../../types/responses';
import { format, formatDuration, intervalToDuration } from 'date-fns';

type TimeSet = {
  startTime: Date | null;
  endTime: Date | null;
  failure: boolean;
};

type Props = {
  visible: boolean;
  onClose: () => void;
  onSave: (sets: TimeSet[]) => void;
  exercise: ExerciseDefinitionResponse;
};

const SetsTimeExerciseDialog: React.FC<Props> = ({
  visible,
  onClose,
  onSave,
  exercise,
}) => {
  const [sets, setSets] = useState<TimeSet[]>([]);
  const [currentSet, setCurrentSet] = useState<number | null>(null);
  const [restTimer, setRestTimer] = useState<string>('00:00');
  const [currentTimer, setCurrentTimer] = useState<string>('00:00');

  useEffect(() => {
    let intervalId: NodeJS.Timeout;
    if (currentSet !== null) {
      if (sets[currentSet].endTime) {
        // Rest timer
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
      } else if (sets[currentSet].startTime) {
        // Current set timer
        intervalId = setInterval(() => {
          const duration = intervalToDuration({
            start: sets[currentSet].startTime!,
            end: new Date(),
          });
          setCurrentTimer(
            formatDuration(duration, {
              format: ['minutes', 'seconds'],
              zero: true,
              delimiter: ':',
            })
          );
        }, 1000);
      }
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
    setCurrentTimer('00:00');
  };

  const endSet = (index: number) => {
    const updatedSets = [...sets];
    updatedSets[index] = {
      ...updatedSets[index],
      endTime: new Date(),
    };
    setSets(updatedSets);
    setRestTimer('00:00');
  };

  const updateSet = (index: number, updates: Partial<TimeSet>) => {
    const updatedSets = [...sets];
    updatedSets[index] = {
      ...updatedSets[index],
      ...updates,
    };
    setSets(updatedSets);
  };

  const getSetDuration = (set: TimeSet) => {
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

          {currentSet !== null && (
            <View style={styles.timerContainer}>
              {sets[currentSet].endTime ? (
                <View style={styles.timer}>
                  <Text style={styles.timerLabel}>Rest Time:</Text>
                  <Text style={styles.timerValue}>{restTimer}</Text>
                </View>
              ) : sets[currentSet].startTime ? (
                <View style={styles.timer}>
                  <Text style={styles.timerLabel}>Current Set:</Text>
                  <Text style={styles.timerValue}>{currentTimer}</Text>
                </View>
              ) : null}
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
                      <View style={styles.failureGroup}>
                        <Text style={styles.inputLabel}>Failure</Text>
                        <Switch
                          value={set.failure}
                          onValueChange={(value) =>
                            updateSet(index, { failure: value })
                          }
                        />
                      </View>
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
  timerContainer: {
    marginBottom: spacing.md,
  },
  timer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.surface,
    padding: spacing.md,
    borderRadius: borderRadius.md,
  },
  timerLabel: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
    color: colors.text,
    marginRight: spacing.sm,
  },
  timerValue: {
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
    justifyContent: 'center',
    gap: spacing.md,
  },
  failureGroup: {
    alignItems: 'center',
  },
  inputLabel: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
    marginBottom: spacing.xs,
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

export default SetsTimeExerciseDialog; 