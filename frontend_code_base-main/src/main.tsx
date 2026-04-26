import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { MsalProvider } from '@azure/msal-react'
import { I18nextProvider } from 'react-i18next'

import './main.css'
import App from './App.tsx'
import i18n from './i18next/i18n'
import { msalInstance } from '@utils/auth.ts'

msalInstance.initialize().then(() => {
  createRoot(document.getElementById('root')!).render(
    <BrowserRouter>
      <MsalProvider instance={msalInstance}>
        <I18nextProvider i18n={i18n}>
          <App />
        </I18nextProvider>
      </MsalProvider>
    </BrowserRouter>,
  )
})
