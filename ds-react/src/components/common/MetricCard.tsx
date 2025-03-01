import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import { colors, typography, spacing, dataCard, metrics } from '../../constants/theme';

export interface MetricCardProps {
  /**
   * The title of the metric
   */
  title: string;
  /**
   * The value to display
   */
  value: string | number;
  /**
   * Optional unit to display after the value
   */
  unit?: string;
  /**
   * Optional trend indicator
   */
  trend?: 'up' | 'down';
  /**
   * Color for the left border of the card
   */
  color: string;
  /**
   * Optional style overrides
   */
  style?: object;
}

export const MetricCard: React.FC<MetricCardProps> = ({ 
  title, 
  value, 
  unit, 
  trend, 
  color,
  style 
}) => (
  <View style={[styles.container, { borderLeftColor: color }, style]}>
    <Text style={styles.title}>{title}</Text>
    <View style={styles.valueContainer}>
      <Text style={styles.value}>{value}</Text>
      {unit && <Text style={styles.unit}>{unit}</Text>}
      {trend && (
        <Icon
          name={trend === 'up' ? 'trending-up' : 'trending-down'}
          size={20}
          color={trend === 'up' ? colors.success : colors.error}
          style={styles.trendIcon}
        />
      )}
    </View>
  </View>
);

const styles = StyleSheet.create({
  container: {
    ...dataCard,
    flex: 1,
    minWidth: metrics.cardMinWidth,
    maxWidth: metrics.cardMaxWidth,
    borderLeftWidth: 4,
  },
  title: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
    marginBottom: spacing.xs,
  },
  valueContainer: {
    flexDirection: 'row',
    alignItems: 'baseline',
  },
  value: {
    fontSize: typography.sizes.xl,
    fontWeight: typography.weights.bold,
    color: colors.text,
  },
  unit: {
    fontSize: typography.sizes.sm,
    color: colors.textSecondary,
    marginLeft: spacing.xs,
  },
  trendIcon: {
    marginLeft: spacing.sm,
  },
}); 