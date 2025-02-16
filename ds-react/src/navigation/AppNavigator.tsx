import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { useUser } from '../context/UserContext';
import { RootStackParamList, AuthStackParamList, MainStackParamList } from './types';

// Auth Screens
import { LoginScreen } from '../screens/auth/LoginScreen';
import { RegisterScreen } from '../screens/auth/RegisterScreen';

// Main App Screens
import { DashboardScreen } from '../screens/DashboardScreen';
import { WorkoutsScreen } from '../screens/workouts/WorkoutsScreen';
import { SleepScreen } from '../screens/sleep/SleepScreen';
import { HeartScreen } from '../screens/heart/HeartScreen';
import { HabitsScreen } from '../screens/habits/HabitsScreen';

const RootStack = createNativeStackNavigator<RootStackParamList>();
const AuthStack = createNativeStackNavigator<AuthStackParamList>();
const MainStack = createNativeStackNavigator<MainStackParamList>();

const AuthNavigator = () => {
  return (
    <AuthStack.Navigator
      screenOptions={{
        headerShown: false,
      }}
    >
      <AuthStack.Screen name="Login" component={LoginScreen} />
      <AuthStack.Screen name="Register" component={RegisterScreen} />
    </AuthStack.Navigator>
  );
};

const MainNavigator = () => {
  return (
    <MainStack.Navigator
      initialRouteName="Dashboard"
      screenOptions={{
        headerShown: false,
      }}
    >
      <MainStack.Screen name="Dashboard" component={DashboardScreen} />
      <MainStack.Screen name="Workouts" component={WorkoutsScreen} />
      <MainStack.Screen name="Sleep" component={SleepScreen} />
      <MainStack.Screen name="Heart" component={HeartScreen} />
      <MainStack.Screen name="Habits" component={HabitsScreen} />
    </MainStack.Navigator>
  );
};

export const AppNavigator: React.FC = () => {
  const { user } = useUser();

  return (
    <RootStack.Navigator
      screenOptions={{
        headerShown: false,
      }}
    >
      {!user ? (
        <RootStack.Screen name="Auth" component={AuthNavigator} />
      ) : (
        <RootStack.Screen name="Main" component={MainNavigator} />
      )}
    </RootStack.Navigator>
  );
}; 