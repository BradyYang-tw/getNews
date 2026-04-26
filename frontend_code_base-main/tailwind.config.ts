import type { Config } from 'tailwindcss'

export default {
  content: ['./src/**/*.{ts,tsx,css}', './src/**/*.module.css'],
  theme: {
    colors: {
      'white-1': 'var(--color-white-1)',

      'red-1': 'var(--color-red-1)',

      'green-1': 'var(--color-green-1)',
      'green-2': 'var(--color-green-2)',

      'blue-1': 'var(--color-blue-1)',
      'blue-2': 'var(--color-blue-2)',
      'blue-3': 'var(--color-blue-3)',
      'blue-4': 'var(--color-blue-4)',
      'blue-5': 'var(--color-blue-5)',
      'blue-6': 'var(--color-blue-6)',

      'gray-1': 'var(--color-gray-1)',
      'gray-2': 'var(--color-gray-2)',
      'gray-3': 'var(--color-gray-3)',
      'gray-4': 'var(--color-gray-4)',
      'gray-5': 'var(--color-gray-5)',
      'gray-6': 'var(--color-gray-6)',
      'gray-7': 'var(--color-gray-7)',
      'gray-8': 'var(--color-gray-8)',
    },
  },
} satisfies Config
