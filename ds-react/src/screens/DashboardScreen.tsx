import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { useUser } from '../context/UserContext';
import { colors, typography, spacing } from '../constants/theme';
import { Button } from '../components/common/Button';

export const DashboardScreen: React.FC<{ navigation: any }> = ({ navigation }) => {
  const { user, setUser } = useUser();

  const handleLogout = () => {
    setUser(null);
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Dashboard</Text>
        <Text style={styles.welcomeText}>Welcome, {user?.username}!</Text>
      </View>

      <Button
        title="Logout"
        variant="outline"
        onPress={handleLogout}
        style={styles.logoutButton}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
    padding: spacing.lg,
  },
  header: {
    marginBottom: spacing.xl,
  },
  title: {
    fontSize: typography.sizes.xxl,
    fontWeight: typography.weights.bold,
    color: colors.text,
  },
  welcomeText: {
    fontSize: typography.sizes.md,
    color: colors.textSecondary,
    marginTop: spacing.xs,
  },
  logoutButton: {
    marginTop: spacing.xl,
  },
}); 