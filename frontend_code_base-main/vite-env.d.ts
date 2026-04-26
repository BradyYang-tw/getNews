/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_AZURE_APPLICATION_ID: string
  readonly VITE_AZURE_TENANT_ID: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
