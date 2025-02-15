import React, { useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { colors, typography, spacing, borderRadius, shadows } from '../../constants/theme';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { DistanceUnit, ExerciseRecordDetailsResponse } from '../../constants/types';

type Props = {
  exerciseId: number;
  exerciseName: string;
  onComplete: (details: ExerciseRecordDetailsResponse) => void;
};

export const DistanceExercise: React.FC<Props> = ({
  exerciseId,
  exerciseName,
  onComplete,
}) => {
  const [distance, setDistance] = useState<number>(0);
  const [unit, setUnit] = useState<DistanceUnit>(DistanceUnit.KILOMETERS);

  const handleComplete = () => {
    onComplete({
      distance,
      distanceUnit: unit,
    });
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>{exerciseName}</Text>

      <View style={styles.card}>
        <View style={styles.inputContainer}>
          <Input
            label="Distance"
            keyboardType="numeric"
            value={distance.toString()}
            onChangeText={(value) => setDistance(parseFloat(value) || 0)}
            style={styles.input}
          />
          <View style={styles.unitSelector}>
            <Text style={styles.unitLabel}>Unit</Text>
            <View style={styles.unitButtons}>
              {Object.values(DistanceUnit).map((distanceUnit) => (
                <Button
                  key={distanceUnit}
                  title={distanceUnit.toLowerCase()}
                  variant={unit === distanceUnit ? 'primary' : 'outline'}
                  onPress={() => setUnit(distanceUnit)}
                  style={styles.unitButton}
                />
              ))}
            </View>
          </View>
        </View>

        <Button
          title="Complete Exercise"
          onPress={handleComplete}
          style={styles.completeButton}
          disabled={distance <= 0}
        />
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
  card: {
    backgroundColor: colors.surface,
    borderRadius: borderRadius.md,
    padding: spacing.md,
    margin: spacing.md,
    ...shadows.sm,
  },
  inputContainer: {
    gap: spacing.md,
    marginBottom: spacing.lg,
  },
  input: {
    marginBottom: 0,
  },
  unitSelector: {
    gap: spacing.sm,
  },
  unitLabel: {
    fontSize: typography.sizes.md,
    color: colors.text,
  },
  unitButtons: {
    flexDirection: 'row',
    gap: spacing.sm,
  },
  unitButton: {
    flex: 1,
  },
  completeButton: {
    marginTop: spacing.md,
  },
}); 