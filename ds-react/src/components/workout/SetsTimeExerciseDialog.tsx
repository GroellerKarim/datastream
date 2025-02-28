import React, { useState, useEffect, useRef } from 'react';
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
import { format, formatDuration, intervalToDuration, Duration } from 'date-fns';

type TimeSet = {
  startTime: Date | null;
  endTime: Date | null;
  failure: boolean;
  restTime?: number; // Rest time in milliseconds
};

type Props = {
  visible: boolean;
  onClose: () => void;
  onSave: (sets: TimeSet[]) => void;
  exercise: ExerciseDefinitionResponse;
  initialRestState?: {
    previousExerciseEndTime: Date | null,
    accumulatedRestTime: number,
    isRestActive: boolean
  } | null;
};

const SetsTimeExerciseDialog: React.FC<Props> = ({
  visible,
  onClose,
  onSave,
  exercise,
  initialRestState = null,
}) => {
  const [sets, setSets] = useState<TimeSet[]>([]);
  const [currentSet, setCurrentSet] = useState<number | null>(null);
  const [restTimer, setRestTimer] = useState<string>('00:00');
  const [currentTimer, setCurrentTimer] = useState<string>('00:00');
  const [isSetInProgress, setIsSetInProgress] = useState(false);
  const [isRestActive, setIsRestActive] = useState(false);
  const restStartTimeRef = useRef<Date | null>(null);
  const accumulatedRestTimeRef = useRef<number>(0);

  useEffect(() => {
    if (visible) {
      // Initialize with basic set
      setSets([{
        startTime: null,
        endTime: null,
        failure: false,
        restTime: 0,
      }]);
      setCurrentSet(null);
      setIsSetInProgress(false);
      setRestTimer('00:00');
      setCurrentTimer('00:00');
      
      // Initialize the rest timer if we have state from a previous exercise
      if (initialRestState && initialRestState.isRestActive && initialRestState.previousExerciseEndTime) {
        restStartTimeRef.current = initialRestState.previousExerciseEndTime;
        accumulatedRestTimeRef.current = initialRestState.accumulatedRestTime;
        setIsRestActive(true);
        
        // Log for debugging
        console.log('Initialized with rest from previous exercise:', {
          previousExerciseEndTime: initialRestState.previousExerciseEndTime,
          accumulatedRestTime: initialRestState.accumulatedRestTime,
          formattedTime: formatMsToTime(initialRestState.accumulatedRestTime)
        });
      } else {
        restStartTimeRef.current = null;
        accumulatedRestTimeRef.current = 0;
        setIsRestActive(false);
      }
    }
  }, [visible, initialRestState]);

  // Rest timer effect
  useEffect(() => {
    let intervalId: NodeJS.Timeout;
    
    if (isRestActive && restStartTimeRef.current) {
      intervalId = setInterval(() => {
        const now = new Date();
        const baseTime = restStartTimeRef.current!;
        const elapsedMs = now.getTime() - baseTime.getTime();
        const totalRestMs = accumulatedRestTimeRef.current + elapsedMs;
        
        const duration = intervalToDuration({
          start: 0,
          end: totalRestMs,
        });
        
        setRestTimer(formatTime(duration));
      }, 1000);
    }
    
    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [isRestActive]);

  // Active set timer effect
  useEffect(() => {
    let intervalId: NodeJS.Timeout;
    if (currentSet !== null && sets[currentSet].startTime && !sets[currentSet].endTime) {
      intervalId = setInterval(() => {
        const duration = intervalToDuration({
          start: sets[currentSet].startTime!,
          end: new Date(),
        });
        setCurrentTimer(formatTime(duration));
      }, 1000);
    }
    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [currentSet, sets]);

  // Format duration to mm:ss
  const formatTime = (duration: Duration) => {
    const minutes = duration.minutes || 0;
    const seconds = duration.seconds || 0;
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  };

  // Format milliseconds to mm:ss
  const formatMsToTime = (ms: number) => {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  const addSet = () => {
    setSets([
      ...sets,
      {
        startTime: null,
        endTime: null,
        failure: false,
        restTime: 0,
      },
    ]);
  };

  const startSet = (index: number) => {
    // Pause the rest timer when starting a set
    if (isRestActive) {
      // Store the accumulated rest time before pausing
      const now = new Date();
      const elapsedSinceRestStart = now.getTime() - (restStartTimeRef.current?.getTime() || now.getTime());
      accumulatedRestTimeRef.current += elapsedSinceRestStart;
      setIsRestActive(false);
    }
    
    setIsSetInProgress(true);
    const updatedSets = [...sets];
    const now = new Date();
    
    // Calculate rest time from previous set or previous exercise
    if (index > 0 && updatedSets[index - 1].endTime) {
      // Rest time from previous set
      updatedSets[index] = {
        ...updatedSets[index],
        startTime: now,
        restTime: accumulatedRestTimeRef.current,
      };
    } else if (index === 0 && initialRestState?.previousExerciseEndTime) {
      // First set with rest time from previous exercise
      updatedSets[index] = {
        ...updatedSets[index],
        startTime: now,
        restTime: accumulatedRestTimeRef.current,
      };
      
      // Log for debugging
      console.log('First set started with rest time from previous exercise:', 
                 accumulatedRestTimeRef.current, 
                 formatMsToTime(accumulatedRestTimeRef.current));
    } else {
      // No rest time
      updatedSets[index] = {
        ...updatedSets[index],
        startTime: now,
        restTime: 0,
      };
    }
    
    setSets(updatedSets);
    setCurrentSet(index);
    setCurrentTimer('00:00');
  };

  const endSet = (index: number) => {
    setIsSetInProgress(false);
    const updatedSets = [...sets];
    const now = new Date();
    updatedSets[index] = {
      ...updatedSets[index],
      endTime: now,
    };
    setSets(updatedSets);
    
    // Reset and start the rest timer
    restStartTimeRef.current = now;
    accumulatedRestTimeRef.current = 0;
    setIsRestActive(true);
    setRestTimer('00:00');
    
    console.log('Set ended, rest timer started at:', now);
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
            {/* Show rest timer if active */}
            {isRestActive && (
              <View style={styles.restTimerContainer}>
                <Text style={styles.restTimerLabel}>Rest Time:</Text>
                <Text style={styles.restTimerValue}>{restTimer}</Text>
              </View>
            )}
            
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
                  
                  {/* Display rest time if available */}
                  {set.restTime && set.restTime > 0 && (
                    <View style={styles.restTimeDisplay}>
                      <Text style={styles.restTimeLabel}>Rest before set:</Text>
                      <Text style={styles.restTimeValue}>{formatMsToTime(set.restTime)}</Text>
                    </View>
                  )}
                </View>
              ))}
            </ScrollView>
          </View>

          <View style={styles.footer}>
            {sets.length > 0 && sets.every(set => set.endTime) ? (
              <View style={styles.footerButtonsContainer}>
                <TouchableOpacity
                  style={styles.addSetButton}
                  onPress={addSet}
                >
                  <Text style={styles.buttonText}>Add Set</Text>
                </TouchableOpacity>
                
                <TouchableOpacity
                  style={styles.completeExerciseButton}
                  onPress={() => onSave(sets)}
                >
                  <Text style={styles.buttonText}>Complete Exercise</Text>
                </TouchableOpacity>
              </View>
            ) : (
              <TouchableOpacity
                style={styles.completeExerciseButton}
                onPress={() => onSave(sets)}
                disabled={sets.length === 0 || sets.some(set => !set.endTime)}
              >
                <Text style={styles.buttonText}>Complete Exercise</Text>
              </TouchableOpacity>
            )}
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
  mainContent: {
    flex: 1,
    marginBottom: spacing.lg,
  },
  restTimerContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    padding: spacing.md,
    backgroundColor: colors.surface,
    borderRadius: borderRadius.md,
    marginBottom: spacing.md,
    borderLeftWidth: 4,
    borderLeftColor: colors.primary,
  },
  restTimerLabel: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
    color: colors.textSecondary,
    marginRight: spacing.sm,
  },
  restTimerValue: {
    fontSize: typography.sizes.lg,
    fontWeight: typography.weights.bold,
    color: colors.primary,
  },
  timerContainer: {
    marginBottom: spacing.md,
  },
  timer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    padding: spacing.md,
    backgroundColor: colors.surface,
    borderRadius: borderRadius.md,
  },
  timerLabel: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
    color: colors.textSecondary,
    marginRight: spacing.sm,
  },
  timerValue: {
    fontSize: typography.sizes.xl,
    fontWeight: typography.weights.bold,
    color: colors.primary,
  },
  setsList: {
    flex: 1,
  },
  setsListContent: {
    paddingVertical: spacing.sm,
  },
  setItem: {
    backgroundColor: colors.surface,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    marginBottom: spacing.sm,
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
    color: colors.primary,
    fontWeight: typography.weights.medium,
  },
  setControls: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  setInputs: {
    flex: 1,
  },
  startButton: {
    backgroundColor: colors.primary,
    paddingVertical: spacing.sm,
    paddingHorizontal: spacing.md,
    borderRadius: borderRadius.md,
    flex: 1,
    alignItems: 'center',
  },
  endButton: {
    backgroundColor: colors.error,
    paddingVertical: spacing.sm,
    paddingHorizontal: spacing.md,
    borderRadius: borderRadius.md,
    flex: 1,
    alignItems: 'center',
  },
  disabledButton: {
    backgroundColor: colors.border,
  },
  disabledButtonText: {
    color: colors.textSecondary,
  },
  buttonText: {
    color: colors.background,
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
  },
  failureGroup: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: spacing.sm,
  },
  inputLabel: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
  },
  completeSetButton: {
    backgroundColor: colors.success,
    paddingVertical: spacing.sm,
    paddingHorizontal: spacing.md,
    borderRadius: borderRadius.md,
    alignItems: 'center',
  },
  footer: {
    marginTop: spacing.md,
  },
  completeExerciseButton: {
    backgroundColor: colors.primary,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    alignItems: 'center',
    flex: 2,
  },
  footerButtonsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: spacing.md,
  },
  addSetButton: {
    backgroundColor: colors.secondary,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    alignItems: 'center',
    flex: 1,
  },
  restTimeDisplay: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: spacing.sm,
    paddingTop: spacing.sm,
    borderTopWidth: 1,
    borderTopColor: colors.border,
  },
  restTimeLabel: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
    marginRight: spacing.sm,
  },
  restTimeValue: {
    fontSize: typography.sizes.sm,
    fontWeight: typography.weights.semibold,
    color: colors.primary,
  },
});

export default SetsTimeExerciseDialog; 