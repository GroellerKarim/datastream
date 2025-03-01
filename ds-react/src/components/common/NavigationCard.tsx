import React from 'react';
import { Pressable, Text, StyleSheet } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import { colors, typography, spacing, shadows, borderRadius } from '../../constants/theme';

export interface NavigationCardProps {
  /**
   * The title to display
   */
  title: string;
  /**
   * The icon name from MaterialCommunityIcons
   */
  icon: string;
  /**
   * Function to call when pressed
   */
  onPress: () => void;
  /**
   * Optional style overrides
   */
  style?: object;
  /**
   * Optional icon color
   */
  iconColor?: string;
  /**
   * Optional icon size
   */
  iconSize?: number;
}

export const NavigationCard: React.FC<NavigationCardProps> = ({ 
  title, 
  icon, 
  onPress,
  style,
  iconColor = colors.primary,
  iconSize = 24
}) => (
  <Pressable 
    onPress={onPress} 
    style={({ pressed }) => [
      styles.container,
      pressed && styles.pressed,
      style
    ]}
  >
    <Icon name={icon} size={iconSize} color={iconColor} />
    <Text style={styles.title}>{title}</Text>
    <Icon name="chevron-right" size={20} color={colors.textSecondary} />
  </Pressable>
);

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: spacing.lg,
    backgroundColor: colors.cardBg,
    borderRadius: borderRadius.lg,
    ...shadows.sm,
  },
  pressed: {
    backgroundColor: colors.cardBgHover,
  },
  title: {
    flex: 1,
    fontSize: typography.sizes.md,
    fontWeight: typography.weights.medium,
    color: colors.text,
    marginLeft: spacing.md,
  },
}); 