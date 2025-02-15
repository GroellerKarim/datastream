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

interface RegisterForm {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export const RegisterScreen: React.FC<{ navigation: any }> = ({ navigation }) => {
  const [form, setForm] = useState<RegisterForm>({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [errors, setErrors] = useState<Partial<RegisterForm>>({});
  const [loading, setLoading] = useState(false);

  const validateEmail = (email: string) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const handleRegister = async () => {
    try {
      setLoading(true);
      setErrors({});

      // Validation
      const newErrors: Partial<RegisterForm> = {};
      if (!form.username) newErrors.username = 'Username is required';
      if (!form.email) newErrors.email = 'Email is required';
      if (!validateEmail(form.email)) newErrors.email = 'Invalid email format';
      if (!form.password) newErrors.password = 'Password is required';
      if (form.password.length < 8) newErrors.password = 'Password must be at least 8 characters';
      if (form.password !== form.confirmPassword) {
        newErrors.confirmPassword = 'Passwords do not match';
      }

      if (Object.keys(newErrors).length > 0) {
        setErrors(newErrors);
        return;
      }

      const response = await fetch('/api/v1/users/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: form.username,
          email: form.email,
          password: form.password,
        }),
      });

      if (!response.ok) {
        throw new Error('Registration failed');
      }

      // Navigate to login screen on success
      navigation.navigate('Login');
      
    } catch (error) {
      setErrors({
        username: 'Registration failed. Please try again.',
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
          <Text style={styles.title}>Create Account</Text>
          <Text style={styles.subtitle}>Join Datastream to track your life data</Text>
        </View>

        <View style={styles.form}>
          <Input
            label="Username"
            value={form.username}
            onChangeText={(text) => setForm({ ...form, username: text })}
            autoCapitalize="none"
            error={errors.username}
            placeholder="Choose a username"
          />

          <Input
            label="Email"
            value={form.email}
            onChangeText={(text) => setForm({ ...form, email: text })}
            autoCapitalize="none"
            keyboardType="email-address"
            error={errors.email}
            placeholder="Enter your email"
          />

          <Input
            label="Password"
            value={form.password}
            onChangeText={(text) => setForm({ ...form, password: text })}
            secureTextEntry
            error={errors.password}
            placeholder="Create a password"
          />

          <Input
            label="Confirm Password"
            value={form.confirmPassword}
            onChangeText={(text) => setForm({ ...form, confirmPassword: text })}
            secureTextEntry
            error={errors.confirmPassword}
            placeholder="Confirm your password"
          />

          <Button
            title="Create Account"
            onPress={handleRegister}
            loading={loading}
            style={styles.registerButton}
          />

          <TouchableOpacity
            onPress={() => navigation.navigate('Login')}
            style={styles.loginLink}
          >
            <Text style={styles.loginText}>
              Already have an account? <Text style={styles.loginTextBold}>Log in</Text>
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
  registerButton: {
    marginTop: spacing.md,
  },
  loginLink: {
    marginTop: spacing.xl,
    alignItems: 'center',
  },
  loginText: {
    fontSize: typography.sizes.md,
    color: colors.textSecondary,
  },
  loginTextBold: {
    color: colors.primary,
    fontWeight: typography.weights.semibold,
  },
}); 