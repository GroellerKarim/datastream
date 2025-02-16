import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  TextInput,
  FlatList,
  ActivityIndicator,
} from 'react-native';
import { colors, spacing, typography, borderRadius } from '../../constants/theme';
import { ExerciseDefinitionResponse } from '../../types/responses';
import { ExerciseType } from '../../constants/types';
import axios from 'axios';
import { API_ENDPOINTS } from '../../config/api';
import { useUser } from '../../context/UserContext';

type Props = {
  visible: boolean;
  onClose: () => void;
  onSelect: (exercise: ExerciseDefinitionResponse) => void;
  workoutTypeId: number;
};

const ExerciseSelectionDialog: React.FC<Props> = ({
  visible,
  onClose,
  onSelect,
  workoutTypeId,
}) => {
  const { user } = useUser();
  const [exercises, setExercises] = useState<ExerciseDefinitionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedType, setSelectedType] = useState<ExerciseType | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchExercises = async () => {
      if (!user?.token) {
        setError('Authentication required');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError(null);
        const response = await axios.get(API_ENDPOINTS.RECENT_EXERCISES(workoutTypeId), {
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        });
        setExercises(response.data);
      } catch (error) {
        console.error('Error fetching exercises:', error);
        setError('Failed to load exercises');
      } finally {
        setLoading(false);
      }
    };

    if (visible) {
      fetchExercises();
    }
  }, [visible, workoutTypeId, user]);

  const filteredExercises = exercises.filter((exercise) => {
    const matchesSearch = exercise.name.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesType = selectedType ? exercise.type === selectedType : true;
    return matchesSearch && matchesType;
  });

  const renderExerciseItem = ({ item }: { item: ExerciseDefinitionResponse }) => (
    <TouchableOpacity style={styles.exerciseItem} onPress={() => onSelect(item)}>
      <View>
        <Text style={styles.exerciseName}>{item.name}</Text>
        <Text style={styles.exerciseType}>{item.type}</Text>
      </View>
    </TouchableOpacity>
  );

  return (
    <Modal visible={visible} animationType="slide" transparent>
      <View style={styles.modalContainer}>
        <View style={styles.content}>
          <View style={styles.header}>
            <Text style={styles.title}>Select Exercise</Text>
            <TouchableOpacity onPress={onClose} style={styles.closeButton}>
              <Text style={styles.closeText}>✕</Text>
            </TouchableOpacity>
          </View>

          <TextInput
            style={styles.searchInput}
            placeholder="Search exercises..."
            value={searchQuery}
            onChangeText={setSearchQuery}
          />

          <View style={styles.typeFilters}>
            <TouchableOpacity
              style={[
                styles.typeFilter,
                !selectedType && styles.typeFilterSelected,
              ]}
              onPress={() => setSelectedType(null)}
            >
              <Text style={[
                styles.typeFilterText,
                !selectedType && styles.typeFilterTextSelected,
              ]}>All</Text>
            </TouchableOpacity>
            {Object.values(ExerciseType).map((type) => (
              <TouchableOpacity
                key={type}
                style={[
                  styles.typeFilter,
                  selectedType === type && styles.typeFilterSelected,
                ]}
                onPress={() => setSelectedType(type)}
              >
                <Text style={[
                  styles.typeFilterText,
                  selectedType === type && styles.typeFilterTextSelected,
                ]}>{type}</Text>
              </TouchableOpacity>
            ))}
          </View>

          {error ? (
            <View style={styles.errorContainer}>
              <Text style={styles.errorText}>{error}</Text>
            </View>
          ) : loading ? (
            <ActivityIndicator size="large" color={colors.primary} />
          ) : (
            <FlatList
              data={filteredExercises}
              renderItem={renderExerciseItem}
              keyExtractor={(item) => item.id.toString()}
              contentContainerStyle={styles.list}
            />
          )}
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
    maxHeight: '80%',
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
  searchInput: {
    backgroundColor: colors.surface,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    marginBottom: spacing.md,
    fontSize: typography.sizes.md,
  },
  typeFilters: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: spacing.sm,
    marginBottom: spacing.md,
  },
  typeFilter: {
    backgroundColor: colors.surface,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    borderRadius: borderRadius.full,
  },
  typeFilterSelected: {
    backgroundColor: colors.primary,
  },
  typeFilterText: {
    color: colors.text,
    fontSize: typography.sizes.sm,
    fontWeight: typography.weights.medium,
  },
  typeFilterTextSelected: {
    color: colors.background,
  },
  list: {
    paddingVertical: spacing.md,
  },
  exerciseItem: {
    backgroundColor: colors.surface,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    marginBottom: spacing.sm,
  },
  exerciseName: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.semibold,
    color: colors.text,
    marginBottom: spacing.xs,
  },
  exerciseType: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
  },
  errorContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: spacing.lg,
  },
  errorText: {
    color: colors.error,
    fontSize: typography.sizes.md,
    textAlign: 'center',
  },
});

export default ExerciseSelectionDialog; 