import React, { useState, useEffect, useRef } from 'react';
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
import { format, formatDuration, intervalToDuration, Duration } from 'date-fns';

type ExerciseSet = {
  startTime: Date | null;
  endTime: Date | null;
  reps: number;
  weight: number;
  failure: boolean;
  partialReps?: number;
  restTime?: number; // Rest time in milliseconds
};

type Props = {
  visible: boolean;
  onClose: () => void;
  onSave: (sets: ExerciseSet[]) => void;
  exercise: ExerciseDefinitionResponse;
  initialRestState?: {
    previousExerciseEndTime: Date | null,
    accumulatedRestTime: number,
    isRestActive: boolean
  } | null;
};

const SetsRepsExerciseDialog: React.FC<Props> = ({
  visible,
  onClose,
  onSave,
  exercise,
  initialRestState = null,
}) => {
  const [sets, setSets] = useState<ExerciseSet[]>([]);
  const [currentSet, setCurrentSet] = useState<number | null>(null);
  const [restTimer, setRestTimer] = useState<string>('00:00');
  const [isSetInProgress, setIsSetInProgress] = useState(false);
  const [expandedSets, setExpandedSets] = useState<{[key: number]: boolean}>({});
  const [activeSetTimer, setActiveSetTimer] = useState<string>('00:00');
  const [isRestActive, setIsRestActive] = useState(false);
  const restStartTimeRef = useRef<Date | null>(null);
  const accumulatedRestTimeRef = useRef<number>(0);

  useEffect(() => {
    if (visible) {
      // Initialize the basic set
      setSets([{
        startTime: null,
        endTime: null,
        reps: 0,
        weight: 0,
        failure: false,
        partialReps: 0,
        restTime: 0,
      }]);
      setCurrentSet(null);
      setIsSetInProgress(false);
      setRestTimer('00:00');
      setActiveSetTimer('00:00');
      setExpandedSets({0: true});
      
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

  useEffect(() => {
    let intervalId: NodeJS.Timeout;
    if (isSetInProgress && currentSet !== null && sets[currentSet].startTime) {
      intervalId = setInterval(() => {
        const duration = intervalToDuration({
          start: sets[currentSet].startTime!,
          end: new Date(),
        });
        setActiveSetTimer(formatTime(duration));
      }, 1000);
    }
    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [isSetInProgress, currentSet, sets]);

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
    const newSetIndex = sets.length;
    setSets([
      ...sets,
      {
        startTime: null,
        endTime: null,
        reps: 0,
        weight: 0,
        failure: false,
        partialReps: 0,
        restTime: 0,
      },
    ]);
    setExpandedSets(prev => ({...prev, [newSetIndex]: true}));
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
    setExpandedSets(prev => ({...prev, [index]: true}));
    setActiveSetTimer('00:00');
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

  const updateSet = (index: number, updates: Partial<ExerciseSet>) => {
    const updatedSets = [...sets];
    updatedSets[index] = {
      ...updatedSets[index],
      ...updates,
    };
    setSets(updatedSets);
  };

  const completeSet = (index: number) => {
    setExpandedSets(prev => ({...prev, [index]: false}));
    
    // Save the current time when completing the set
    const completionTime = new Date();
    const updatedSets = [...sets];
    
    // Make sure the endTime is set accurately
    if (updatedSets[index].startTime && !updatedSets[index].endTime) {
      updatedSets[index] = {
        ...updatedSets[index],
        endTime: completionTime,
      };
      setSets(updatedSets);
    }
    
    // Log completion time for debugging
    console.log('Set completed at:', completionTime, 'Rest time stored:', updatedSets[index].restTime);
    
    const nextSetIndex = index + 1;
    if (nextSetIndex < sets.length) {
      setExpandedSets(prev => ({...prev, [nextSetIndex]: true}));
    }
    
    setCurrentSet(null);
  };

  const toggleSetExpansion = (index: number) => {
    setExpandedSets(prev => ({...prev, [index]: !prev[index]}));
  };

  const getSetDuration = (set: ExerciseSet) => {
    if (!set.startTime || !set.endTime) return '00:00';
    const durationInMs = set.endTime.getTime() - set.startTime.getTime();
    const seconds = Math.floor(durationInMs / 1000);
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  const renderSetSummary = (set: ExerciseSet) => {
    if (!set.endTime) return null;
    
    return (
      <View style={styles.setSummary}>
        <Text style={styles.summaryText}>
          {`${set.weight} kg × ${set.reps} reps`}
          {set.failure ? <Text>{` (to failure${set.partialReps ? ` +${set.partialReps} p` : ''})`}</Text> : null}
        </Text>
        <View style={styles.timersContainer}>
          <View style={styles.timerItem}>
            <Text style={styles.timerLabel}>Duration:</Text>
            <Text style={styles.timerValue}>{getSetDuration(set)}</Text>
          </View>
          {set.restTime && set.restTime > 0 ? (
            <View style={styles.timerItem}>
              <Text style={styles.timerLabel}>Rest:</Text>
              <Text style={styles.timerValue}>{formatMsToTime(set.restTime)}</Text>
            </View>
          ) : null}
        </View>
      </View>
    );
  };

  // Debug function to log set data - can be removed after fixing
  const logSetData = (set: ExerciseSet, index: number) => {
    console.log(`Set ${index + 1} data:`, {
      weight: set.weight,
      reps: set.reps,
      restTime: set.restTime,
      startTime: set.startTime,
      endTime: set.endTime,
      restTimeFormatted: set.restTime ? formatMsToTime(set.restTime) : 'N/A'
    });
  };

  const renderExpandIcon = (isExpanded: boolean) => {
    return (
      <Text style={styles.expandCollapseIcon}>
        {isExpanded ? '▼' : '▶'}
      </Text>
    );
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
            {/* Show rest timer whenever it's active or there's a completed set */}
            {(isRestActive || sets.some(set => set.endTime)) && (
              <View style={[
                styles.restTimer, 
                !isRestActive && styles.restTimerPaused
              ]}>
                <Text style={styles.restTimerLabel}>
                  Rest Time {!isRestActive && "(Paused)"}:
                </Text>
                <Text style={[
                  styles.restTimerValue, 
                  !isRestActive && styles.restTimerValuePaused
                ]}>
                  {restTimer}
                </Text>
              </View>
            )}

            <ScrollView style={styles.setsList} contentContainerStyle={styles.setsListContent}>
              {sets.map((set, index) => {
                // Log set data to debug rest time issues
                if (set.endTime) {
                  logSetData(set, index);
                }
                
                return (
                <View key={index} style={styles.setItem}>
                  <TouchableOpacity 
                    style={styles.setHeader} 
                    onPress={() => set.endTime ? toggleSetExpansion(index) : null}
                    disabled={!set.endTime}
                  >
                    <Text style={styles.setTitle}>Set {index + 1}</Text>
                    {set.endTime ? (
                      <View style={styles.setHeaderRight}>
                        {!expandedSets[index] ? renderSetSummary(set) : null}
                        {renderExpandIcon(expandedSets[index])}
                      </View>
                    ) : null}
                  </TouchableOpacity>

                  {(!set.endTime || expandedSets[index]) ? (
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
                        <View style={styles.activeSetContainer}>
                          <View style={styles.activeSetTimer}>
                            <Text style={styles.activeSetTimerLabel}>Active:</Text>
                            <Text style={styles.activeSetTimerValue}>{activeSetTimer}</Text>
                          </View>
                          <TouchableOpacity
                            style={styles.endButton}
                            onPress={() => endSet(index)}
                          >
                            <Text style={styles.buttonText}>Stop Set</Text>
                          </TouchableOpacity>
                        </View>
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
                          
                          {set.restTime && set.restTime > 0 ? (
                            <View style={styles.restSummary}>
                              <Text style={styles.restSummaryLabel}>Rest time before this set:</Text>
                              <Text style={styles.restSummaryValue}>{formatMsToTime(set.restTime)}</Text>
                            </View>
                          ) : null}
                          
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
                  ) : null}
                </View>
                );
              })}
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
    marginVertical: spacing.md,
  },
  restTimerPaused: {
    backgroundColor: colors.surfaceHover,
    borderWidth: 1,
    borderColor: colors.border,
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
  restTimerValuePaused: {
    color: colors.textSecondary,
  },
  activeSetContainer: {
    gap: spacing.md,
  },
  activeSetTimer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.surfaceHover,
    padding: spacing.md,
    borderRadius: borderRadius.md,
  },
  activeSetTimerLabel: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
    color: colors.text,
    marginRight: spacing.sm,
  },
  activeSetTimerValue: {
    fontSize: typography.sizes.lg,
    fontWeight: typography.weights.bold,
    color: colors.dataOrange,
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
  setHeaderRight: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: spacing.sm,
  },
  setTitle: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.semibold,
    color: colors.text,
  },
  expandCollapseIcon: {
    fontSize: typography.sizes.md,
    color: colors.textSecondary,
  },
  setSummary: {
    flexDirection: 'column',
    gap: spacing.xs,
    maxWidth: '80%',
  },
  summaryText: {
    fontSize: typography.sizes.sm,
    fontWeight: typography.weights.medium,
    color: colors.text,
  },
  timersContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    flexWrap: 'wrap',
    gap: spacing.sm,
  },
  timerItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginRight: spacing.xs,
  },
  timerLabel: {
    fontSize: typography.sizes.xs,
    color: colors.textSecondary,
    marginRight: spacing.xs,
  },
  timerValue: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
    fontWeight: typography.weights.medium,
  },
  durationText: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
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
  restSummary: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.surfaceHover,
    padding: spacing.sm,
    borderRadius: borderRadius.md,
    marginBottom: spacing.xs,
  },
  restSummaryLabel: {
    fontSize: typography.sizes.sm,
    color: colors.text,
    marginRight: spacing.sm,
  },
  restSummaryValue: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.semibold,
    color: colors.primary,
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
    flex: 2,
  },
  footerButtonsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: spacing.md,
    marginTop: spacing.lg,
  },
  addSetButton: {
    backgroundColor: colors.secondary,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    flex: 1,
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