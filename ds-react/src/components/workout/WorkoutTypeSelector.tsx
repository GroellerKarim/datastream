import React, { useState } from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity } from 'react-native';
import { colors, typography, spacing, borderRadius, shadows } from '../../constants/theme';
import { Input } from '../common/Input';

type WorkoutType = {
  id: number;
  name: string;
};

type Props = {
  workoutTypes: WorkoutType[];
  onSelect: (workoutType: WorkoutType) => void;
};

export const WorkoutTypeSelector: React.FC<Props> = ({ workoutTypes, onSelect }) => {
  const [searchQuery, setSearchQuery] = useState('');

  const filteredTypes = workoutTypes.filter((type) =>
    type.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const renderWorkoutTypeItem = ({ item }: { item: WorkoutType }) => (
    <TouchableOpacity
      style={styles.workoutTypeItem}
      onPress={() => onSelect(item)}
    >
      <Text style={styles.workoutTypeName}>{item.name}</Text>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.searchContainer}>
        <Input
          placeholder="Search workout types..."
          value={searchQuery}
          onChangeText={setSearchQuery}
          style={styles.searchInput}
        />
      </View>

      <Text style={styles.sectionTitle}>All Workout Types</Text>
      <FlatList
        data={filteredTypes}
        renderItem={renderWorkoutTypeItem}
        keyExtractor={(item) => item.id.toString()}
        style={styles.workoutTypeList}
        contentContainerStyle={styles.workoutTypeListContent}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  searchContainer: {
    padding: spacing.md,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
  },
  searchInput: {
    marginBottom: 0,
  },
  sectionTitle: {
    fontSize: typography.sizes.lg,
    fontWeight: typography.weights.semibold,
    color: colors.text,
    marginBottom: spacing.md,
  },
  workoutTypeList: {
    flex: 1,
  },
  workoutTypeListContent: {
    padding: spacing.md,
  },
  workoutTypeItem: {
    padding: spacing.md,
    backgroundColor: colors.surface,
    borderRadius: borderRadius.md,
    marginBottom: spacing.sm,
    ...shadows.sm,
  },
  workoutTypeName: {
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
    color: colors.text,
  },
}); 