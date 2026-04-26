import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { federation } from '@module-federation/vite'
import * as path from 'path'
import tsconfigPaths from 'vite-tsconfig-paths'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const root = process.cwd()
  const viteEnv = loadEnv(mode, root)

  return {
    base: '/',
    plugins: [
      react(),
      tsconfigPaths(),
      tailwindcss(),
      federation({
        name: 'rd_lab',
        remotes: {
          ov_viewer: {
            type: 'module',
            name: 'ov_viewer',
            entry: `${viteEnv.VITE_OV_VIEWER_URL}/remoteEntry.js`,
          },
        },
        manifest: true,
      }),
    ],
    server: {
      host: true,
      strictPort: true,
      port: 3000,
      allowedHosts: ['react-frontend', '.local'],
    },
    build: {
      target: 'esnext',
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'),
        '@apis': path.resolve(__dirname, 'src/apis'),
        '@store': path.resolve(__dirname, 'src/store'),
        '@components': path.resolve(__dirname, 'src/components'),
        '@assets': path.resolve(__dirname, 'src/assets'),
        '@routes': path.resolve(__dirname, 'src/routes'),
        '@typing': path.resolve(__dirname, 'src/typing'),
        '@style': path.resolve(__dirname, 'src/style'),
        '@hook': path.resolve(__dirname, 'src/hook'),
        '@config': path.resolve(__dirname, 'src/config'),
        '@utils': path.resolve(__dirname, 'src/utils'),
        '@pages': path.resolve(__dirname, 'src/pages'),
      },
    },
  }
})
