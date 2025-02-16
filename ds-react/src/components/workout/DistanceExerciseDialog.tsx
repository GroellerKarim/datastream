import React, { useState } from 'react';
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
import { ExerciseDefinitionResponse } from '../../types/responses';
import { DistanceUnit } from '../../constants/types';
import { Picker } from '@react-native-picker/picker';

type Props = {
  visible: boolean;
  onClose: () => void;
  onSave: (data: { distance: number; unit: DistanceUnit }) => void;
  exercise: ExerciseDefinitionResponse;
};

const DistanceExerciseDialog: React.FC<Props> = ({
  visible,
  onClose,
  onSave,
  exercise,
}) => {
  const [distance, setDistance] = useState<string>('');
  const [unit, setUnit] = useState<DistanceUnit>(DistanceUnit.KILOMETERS);

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