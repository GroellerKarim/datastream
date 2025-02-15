import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Modal, FlatList, TouchableOpacity } from 'react-native';
import { colors, typography, spacing, borderRadius, shadows } from '../../constants/theme';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { ExerciseType } from '../../constants/types';
import axios from 'axios';
import { API_BASE_URL } from '../../config/api';
import { useUser } from '../../context/UserContext';

type ExerciseDefinition = {
  id: number;
  name: string;
  type: ExerciseType;
};

type Props = {
  onSelect: (exercise: ExerciseDefinition) => void;
  workoutTypeId: number;
};

export const ExerciseSelector: React.FC<Props> = ({ onSelect, workoutTypeId }) => {
  const { user } = useUser();
  const [isVisible, setIsVisible] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedType, setSelectedType] = useState<ExerciseType | null>(null);
  const [exercises, setExercises] = useState<ExerciseDefinition[]>([]);
  const [recentExercises, setRecentExercises] = useState<ExerciseDefinition[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (isVisible) {
      fetchExercises();
      fetchRecentExercises();
    }
  }, [isVisible, workoutTypeId]);

  const fetchExercises = async () => {
    if (!user?.token) return;

    try {
      setIsLoading(true);
      setError(null);
      const response = await axios.get<ExerciseDefinition[]>(
        `${API_BASE_URL}/api/v1/exercises/all`,
        {
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        }
      );
      setExercises(response.data);
    } catch (err) {
      setError('Failed to load exercises');
      console.error('Error fetching exercises:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchRecentExercises = async () => {
    if (!user?.token) return;

    try {
      const response = await axios.get<ExerciseDefinition[]>(
        `${API_BASE_URL}/api/v1/exercises/recent/${workoutTypeId}`,
        {
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        }
      );
      setRecentExercises(response.data);
    } catch (err) {
      console.error('Error fetching recent exercises:', err);
    }
  };

  const filteredExercises = exercises.filter((exercise) => {
    const matchesSearch = exercise.name.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesType = !selectedType || exercise.type === selectedType;
    return matchesSearch && matchesType;
  });

  const handleSelect = (exercise: ExerciseDefinition) => {
    onSelect(exercise);
    setIsVisible(false);
    setSearchQuery('');
    setSelectedType(null);
  };

  const renderExerciseItem = ({ item }: { item: ExerciseDefinition }) => (
    <TouchableOpacity
      style={styles.exerciseItem}
      onPress={() => handleSelect(item)}
    >
      <View>
        <Text style={styles.exerciseName}>{item.name}</Text>
        <Text style={styles.exerciseType}>{item.type}</Text>
      </View>
    </TouchableOpacity>
  );

  return (
    <>
      <Button
        title="Select Exercise"
        onPress={() => setIsVisible(true)}
        style={styles.selectButton}
      />

      <Modal
        visible={isVisible}
        animationType="slide"
        presentationStyle="pageSheet"
      >
        <View style={styles.modalContainer}>
          <View style={styles.modalHeader}>
            <Text style={styles.modalTitle}>Select Exercise</Text>
            <Button
              title="Close"
              variant="outline"
              onPress={() => setIsVisible(false)}
            />
          </View>

          <View style={styles.searchContainer}>
            <Input
              placeholder="Search exercises..."
              value={searchQuery}
              onChangeText={setSearchQuery}
              style={styles.searchInput}
            />
            <View style={styles.filterButtons}>
              {Object.values(ExerciseType).map((type) => (
                <Button
                  key={type}
                  title={type}
                  variant={selectedType === type ? 'primary' : 'outline'}
                  onPress={() => setSelectedType(selectedType === type ? null : type)}
                  style={styles.filterButton}
                />
              ))}
            </View>
          </View>

          {recentExercises.length > 0 && searchQuery === '' && !selectedType && (
            <>
              <Text style={styles.sectionTitle}>Recent Exercises</Text>
              <FlatList
                data={recentExercises}
                renderItem={renderExerciseItem}
                keyExtractor={(item) => item.id.toString()}
                horizontal
                style={styles.recentList}
                showsHorizontalScrollIndicator={false}
              />
            </>
          )}

          <Text style={styles.sectionTitle}>All Exercises</Text>
          <FlatList
            data={filteredExercises}
            renderItem={renderExerciseItem}
            keyExtractor={(item) => item.id.toString()}
            style={styles.exerciseList}
            contentContainerStyle={styles.exerciseListContent}
          />
        </View>
      </Modal>
    </>
  );
};

const styles = StyleSheet.create({
  selectButton: {
    marginBottom: spacing.md,
  },
  modalContainer: {
    flex: 1,
    backgroundColor: colors.background,
    paddingTop: spacing.xl,
  },
  modalHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: spacing.md,
    paddingBottom: spacing.md,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
  },
  modalTitle: {
    fontSize: typography.sizes.xl,
    fontWeight: typography.weights.semibold,
    color: colors.text,
  },
  searchContainer: {
    padding: spacing.md,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
  },
  searchInput: {
    marginBottom: spacing.sm,
  },
  filterButtons: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: spacing.sm,
  },
  filterButton: {
    paddingHorizontal: spacing.md,
  },
  sectionTitle: {
    fontSize: typography.sizes.lg,
    fontWeight: typography.weights.semibold,
    color: colors.text,
    padding: spacing.md,
  },
  recentList: {
    maxHeight: 100,
  },
  exerciseList: {
    flex: 1,
  },
  exerciseListContent: {
    padding: spacing.md,
  },
  exerciseItem: {
    padding: spacing.md,
    backgroundColor: colors.surface,
    borderRadius: borderRadius.md,
    marginBottom: spacing.sm,
    ...shadows.sm,
  },
  exerciseName: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
    color: colors.text,
    marginBottom: spacing.xs,
  },
  exerciseType: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
  },
}); 