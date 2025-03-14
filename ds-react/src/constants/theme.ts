export const colors = {
  primary: '#2563EB',
  secondary: '#3B82F6',
  background: '#FFFFFF',
  surface: '#F3F4F6',
  surfaceHover: '#E5E7EB',
  text: '#1F2937',
  textSecondary: '#6B7280',
  error: '#DC2626',
  success: '#059669',
  border: '#E5E7EB',
  // Data visualization colors
  dataBlue: '#5794F2',
  dataGreen: '#73BF69',
  dataOrange: '#FF9830',
  dataPurple: '#8F3BB8',
  // Card backgrounds
  cardBg: '#FAFAFA',
  cardBgHover: '#F3F4F6',
} as const;

export const typography = {
  sizes: {
    xs: 12,
    sm: 14,
    md: 16,
    lg: 18,
    xl: 20,
    xxl: 24,
  },
  weights: {
    regular: '400',
    medium: '500',
    semibold: '600',
    bold: '700',
  },
} as const;

export const spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
  xxl: 48,
} as const;

export const borderRadius = {
  sm: 4,
  md: 8,
  lg: 12,
  xl: 16,
  full: 9999,
} as const;

export const shadows = {
  sm: {
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 1,
    },
    shadowOpacity: 0.18,
    shadowRadius: 1.0,
    elevation: 1,
  },
  md: {
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
  },
} as const;

export const metrics = {
  cardMinWidth: 150,
  cardMaxWidth: 300,
  gridGap: spacing.md,
} as const;

export const dataCard = {
  padding: spacing.lg,
  borderRadius: borderRadius.lg,
  backgroundColor: colors.cardBg,
  ...shadows.sm,
} as const; 