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
import { ExerciseDefinitionResponse } from '../../constants/types';
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
  const [isSetInProgress, setIsSetInProgress] = useState(false);

  useEffect(() => {
    if (visible) {
      setSets([{
        startTime: null,
        endTime: null,
        failure: false,
      }]);
      setCurrentSet(null);
      setIsSetInProgress(false);
      setRestTimer('00:00');
      setCurrentTimer('00:00');
    }
  }, [visible]);

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
    setIsSetInProgress(true);
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
    setIsSetInProgress(false);
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

  const completeSet = (index: number) => {
    if (index === sets.length - 1) {
      // Add a new set automatically when completing the last set
      addSet();
    }
    setCurrentSet(null);
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

          <View style={styles.mainContent}>
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

            <ScrollView style={styles.setsList} contentContainerStyle={styles.setsListContent}>
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
                        <View style={styles.failureGroup}>
                          <Text style={styles.inputLabel}>Failure</Text>
                          <Switch
                            value={set.failure}
                            onValueChange={(value) =>
                              updateSet(index, { failure: value })
                            }
                          />
                        </View>
                        <TouchableOpacity
                          style={styles.completeSetButton}
                          onPress={() => completeSet(index)}
                        >
                          <Text style={styles.buttonText}>Complete Set</Text>
                        </TouchableOpacity>
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
    padding: spacing.lg,
    borderTopWidth: 1,
    borderTopColor: colors.border,
    backgroundColor: colors.background,
  },
  completeSetButton: {
    backgroundColor: colors.success,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    marginTop: spacing.md,
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
});

export default SetsTimeExerciseDialog; 