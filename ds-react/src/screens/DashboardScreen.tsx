import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useUser } from '../context/UserContext';
import { colors, typography, spacing } from '../constants/theme';
import { Button } from '../components/common/Button';
import { MetricCard } from '../components/common/MetricCard';
import { NavigationCard } from '../components/common/NavigationCard';
import { MainStackParamList } from '../navigation/types';

type Props = NativeStackScreenProps<MainStackParamList, 'Dashboard'>;

export const DashboardScreen: React.FC<Props> = ({ navigation }) => {
  const { user, setUser } = useUser();
  const insets = useSafeAreaInsets();

  // Dummy data for metrics with proper typing
  const metrics: Array<Parameters<typeof MetricCard>[0]> = [
    { title: 'Sleep Score', value: 85, unit: '%', trend: 'up' as const, color: colors.dataBlue },
    { title: 'Sleep Duration', value: '7.5', unit: 'hrs', trend: 'down' as const, color: colors.dataPurple },
    { title: 'HRV', value: 65, unit: 'ms', trend: 'up' as const, color: colors.dataGreen },
    { title: 'RHR', value: 62, unit: 'bpm', trend: 'down' as const, color: colors.dataOrange },
  ];

  const navigationItems = [
    { title: 'Workouts & Exercises', icon: 'dumbbell', screen: 'Workouts' as const },
    { title: 'Sleep Analysis', icon: 'sleep', screen: 'Sleep' as const },
    { title: 'Heart Data', icon: 'heart-pulse', screen: 'Heart' as const },
    { title: 'Habits', icon: 'calendar-check', screen: 'Habits' as const },
  ] as const;

  const handleLogout = () => {
    setUser(null);
  };

  return (
    <ScrollView 
      style={[styles.container, { paddingTop: insets.top }]} 
      contentContainerStyle={[
        styles.contentContainer,
        { paddingBottom: insets.bottom }
      ]}
      showsVerticalScrollIndicator={false}
    >
      <View style={styles.header}>
        <View>
          <Text style={styles.welcomeText}>Welcome back,</Text>
          <Text style={styles.username}>{user?.username}</Text>
        </View>
        <Button
          title="Logout"
          variant="outline"
          onPress={handleLogout}
          style={styles.logoutButton}
        />
      </View>

      <View style={styles.metricsGrid}>
        {metrics.map((metric, index) => (
          <MetricCard key={index} {...metric} />
        ))}
      </View>

      <Text style={styles.sectionTitle}>Track & Analyze</Text>
      <View style={styles.navigationSection}>
        {navigationItems.map((item, index) => (
          <NavigationCard
            key={index}
            title={item.title}
            icon={item.icon}
            onPress={() => navigation.navigate(item.screen)}
          />
        ))}
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  contentContainer: {
    flexGrow: 1,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: spacing.lg,
    paddingBottom: spacing.md,
  },
  welcomeText: {
    fontSize: typography.sizes.md,
    color: colors.textSecondary,
  },
  username: {
    fontSize: typography.sizes.xl,
    fontWeight: typography.weights.bold,
    color: colors.text,
  },
  logoutButton: {
    minWidth: 100,
  },
  metricsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    padding: spacing.md,
    gap: spacing.md,
  },
  sectionTitle: {
    fontSize: typography.sizes.lg,
    fontWeight: typography.weights.bold,
    color: colors.text,
    padding: spacing.lg,
    paddingBottom: spacing.md,
  },
  navigationSection: {
    padding: spacing.md,
    gap: spacing.md,
  },
}); 