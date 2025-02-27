import React, { useState, useEffect, useRef } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  TextInput,
  ScrollView,
} from 'react-native';
import { colors, spacing, typography, borderRadius } from '../../constants/theme';
import { ExerciseDefinitionResponse, DistanceUnit } from '../../constants/types';
import { Picker } from '@react-native-picker/picker';
import { intervalToDuration, Duration } from 'date-fns';

type Props = {
  visible: boolean;
  onClose: () => void;
  onSave: (data: { distance: number; unit: DistanceUnit }) => void;
  exercise: ExerciseDefinitionResponse;
  initialRestState?: {
    previousExerciseEndTime: Date | null,
    accumulatedRestTime: number,
    isRestActive: boolean
  } | null;
};

const DistanceExerciseDialog: React.FC<Props> = ({
  visible,
  onClose,
  onSave,
  exercise,
  initialRestState = null,
}) => {
  const [distance, setDistance] = useState<string>('');
  const [unit, setUnit] = useState<DistanceUnit>(DistanceUnit.KILOMETERS);
  const [isRestActive, setIsRestActive] = useState(false);
  const [restTimer, setRestTimer] = useState<string>('00:00');
  const restStartTimeRef = useRef<Date | null>(null);
  const accumulatedRestTimeRef = useRef<number>(0);

  useEffect(() => {
    if (visible) {
      setDistance('');
      setUnit(DistanceUnit.KILOMETERS);
      
      // Initialize the rest timer if we have state from a previous exercise
      if (initialRestState && initialRestState.isRestActive && initialRestState.previousExerciseEndTime) {
        restStartTimeRef.current = initialRestState.previousExerciseEndTime;
        accumulatedRestTimeRef.current = initialRestState.accumulatedRestTime;
        setIsRestActive(true);
        
        // Log for debugging
        console.log('Distance dialog initialized with rest from previous exercise:', {
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

  const handleSave = () => {
    const distanceValue = parseFloat(distance);
    if (isNaN(distanceValue) || distanceValue <= 0) return;
    onSave({ distance: distanceValue, unit });
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

          {/* Rest Timer Display */}
          {isRestActive && (
            <View style={styles.restTimerContainer}>
              <Text style={styles.restTimerLabel}>Rest Time:</Text>
              <Text style={styles.restTimerValue}>{restTimer}</Text>
            </View>
          )}

          <ScrollView style={styles.form}>
            <View style={styles.inputGroup}>
              <Text style={styles.inputLabel}>Distance</Text>
              <TextInput
                style={styles.input}
                keyboardType="numeric"
                value={distance}
                onChangeText={setDistance}
                placeholder="Enter distance"
              />
            </View>

            <View style={styles.inputGroup}>
              <Text style={styles.inputLabel}>Unit</Text>
              <View style={styles.pickerContainer}>
                <Picker
                  selectedValue={unit}
                  onValueChange={(value) => setUnit(value as DistanceUnit)}
                  style={styles.picker}
                >
                  {Object.values(DistanceUnit).map((value) => (
                    <Picker.Item key={value} label={value} value={value} />
                  ))}
                </Picker>
              </View>
            </View>
          </ScrollView>

          <TouchableOpacity
            style={[
              styles.saveButton,
              (!distance || parseFloat(distance) <= 0) && styles.saveButtonDisabled,
            ]}
            onPress={handleSave}
            disabled={!distance || parseFloat(distance) <= 0}
          >
            <Text style={styles.buttonText}>Save Exercise</Text>
          </TouchableOpacity>
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
  form: {
    flex: 1,
  },
  inputGroup: {
    marginBottom: spacing.lg,
  },
  inputLabel: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
    marginBottom: spacing.xs,
  },
  input: {
    backgroundColor: colors.surface,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    fontSize: typography.sizes.md,
  },
  pickerContainer: {
    backgroundColor: colors.surface,
    borderRadius: borderRadius.md,
    overflow: 'hidden',
  },
  picker: {
    height: 50,
  },
  saveButton: {
    backgroundColor: colors.primary,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    alignItems: 'center',
    marginTop: spacing.md,
  },
  saveButtonDisabled: {
    opacity: 0.5,
  },
  buttonText: {
    color: colors.background,
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.semibold,
  },
});

export default DistanceExerciseDialog; 