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
  SectionList,
} from 'react-native';
import { colors, spacing, typography, borderRadius } from '../../constants/theme';
import { ExerciseDefinitionResponse, ExerciseType } from '../../constants/types';
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
  const [recentExercises, setRecentExercises] = useState<ExerciseDefinitionResponse[]>([]);
  const [allExercises, setAllExercises] = useState<ExerciseDefinitionResponse[]>([]);
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
        
        const [recentResponse, allResponse] = await Promise.all([
          axios.get(API_ENDPOINTS.RECENT_EXERCISES(workoutTypeId), {
            headers: { Authorization: `Bearer ${user.token}` },
          }),
          axios.get(API_ENDPOINTS.ALL_EXERCISES, {
            headers: { Authorization: `Bearer ${user.token}` },
          }),
        ]);

        setRecentExercises(recentResponse.data);
        setAllExercises(allResponse.data);
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

  const filterExercises = (exercises: ExerciseDefinitionResponse[]) => {
    return exercises.filter((exercise) => {
      const matchesSearch = exercise.name.toLowerCase().includes(searchQuery.toLowerCase());
      const matchesType = selectedType ? exercise.type === selectedType : true;
      return matchesSearch && matchesType;
    });
  };

  const sections = [
    {
      title: 'Recent Exercises',
      data: filterExercises(recentExercises),
    },
    {
      title: 'All Exercises',
      data: filterExercises(allExercises.filter(
        exercise => !recentExercises.some(recent => recent.id === exercise.id)
      )),
    },
  ];

  const renderSectionHeader = ({ section: { title } }: { section: { title: string } }) => (
    <View style={styles.sectionHeader}>
      <Text style={styles.sectionTitle}>{title}</Text>
    </View>
  );

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
            {Object.values(ExerciseType).map((type) => (
              <TouchableOpacity
                key={type}
                style={[
                  styles.typeFilter,
                  selectedType === type && styles.typeFilterSelected,
                ]}
                onPress={() => setSelectedType(type === selectedType ? null : type)}
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
            <SectionList
              sections={sections}
              renderItem={renderExerciseItem}
              renderSectionHeader={renderSectionHeader}
              keyExtractor={(item) => item.id.toString()}
              contentContainerStyle={styles.list}
              stickySectionHeadersEnabled={true}
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
    height: '80%',
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
    backgroundColor: colors.background,
  },
  errorText: {
    color: colors.error,
    fontSize: typography.sizes.md,
    textAlign: 'center',
  },
  sectionHeader: {
    backgroundColor: colors.background,
    paddingVertical: spacing.sm,
    paddingHorizontal: spacing.md,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
  },
  sectionTitle: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.bold,
    color: colors.textSecondary,
  },
});

export default ExerciseSelectionDialog; 