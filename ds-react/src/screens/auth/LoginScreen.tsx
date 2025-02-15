import React, { useState } from 'react';
import {
  View,
  StyleSheet,
  Text,
  TouchableOpacity,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
} from 'react-native';
import { colors, typography, spacing } from '../../constants/theme';
import { Input } from '../../components/common/Input';
import { Button } from '../../components/common/Button';

interface LoginForm {
  username: string;
  password: string;
}

export const LoginScreen: React.FC<{ navigation: any }> = ({ navigation }) => {
  const [form, setForm] = useState<LoginForm>({
    username: '',
    password: '',
  });
  const [errors, setErrors] = useState<Partial<LoginForm>>({});
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    try {
      setLoading(true);
      setErrors({});

      // Basic validation
      const newErrors: Partial<LoginForm> = {};
      if (!form.username) newErrors.username = 'Username is required';
      if (!form.password) newErrors.password = 'Password is required';
      
      if (Object.keys(newErrors).length > 0) {
        setErrors(newErrors);
        return;
      }

      const response = await fetch('/api/v1/users/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: form.username,
          password: form.password,
        }),
      });

      if (!response.ok) {
        throw new Error('Login failed');
      }

      const data = await response.json();
      // Handle successful login (store token, navigate, etc.)
      
    } catch (error) {
      setErrors({
        username: 'Invalid username or password',
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      style={styles.container}
    >
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled"
      >
        <View style={styles.header}>
          <Text style={styles.title}>Welcome to Datastream</Text>
          <Text style={styles.subtitle}>Track your life data in one place</Text>
        </View>

        <View style={styles.form}>
          <Input
            label="Username"
            value={form.username}
            onChangeText={(text) => setForm({ ...form, username: text })}
            autoCapitalize="none"
            error={errors.username}
            placeholder="Enter your username"
          />

          <Input
            label="Password"
            value={form.password}
            onChangeText={(text) => setForm({ ...form, password: text })}
            secureTextEntry
            error={errors.password}
            placeholder="Enter your password"
          />

          <Button
            title="Log In"
            onPress={handleLogin}
            loading={loading}
            style={styles.loginButton}
          />

          <TouchableOpacity
            onPress={() => navigation.navigate('Register')}
            style={styles.registerLink}
          >
            <Text style={styles.registerText}>
              Don't have an account? <Text style={styles.registerTextBold}>Sign up</Text>
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  scrollContent: {
    flexGrow: 1,
    padding: spacing.lg,
  },
  header: {
    marginTop: spacing.xl,
    marginBottom: spacing.xl,
  },
  title: {
    fontSize: typography.sizes.xxl,
    fontWeight: typography.weights.bold,
    color: colors.text,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: typography.sizes.md,
    color: colors.textSecondary,
    textAlign: 'center',
    marginTop: spacing.xs,
  },
  form: {
    marginTop: spacing.lg,
  },
  loginButton: {
    marginTop: spacing.md,
  },
  registerLink: {
    marginTop: spacing.xl,
    alignItems: 'center',
  },
  registerText: {
    fontSize: typography.sizes.md,
    color: colors.textSecondary,
  },
  registerTextBold: {
    color: colors.primary,
    fontWeight: typography.weights.semibold,
  },
}); 