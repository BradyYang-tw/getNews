import React, { useEffect } from 'react'
import { ConfigProvider } from 'antd'
import { AuthenticatedTemplate, UnauthenticatedTemplate, useMsal } from '@azure/msal-react'

import { loginRequest } from '@config/authConfig'
import { AuthenticatedRoutes, UnauthenticatedRoutes } from '@routes/routes'
import NotificationModal from '@components/compose/NotificationModal'

const App: React.FC = () => {
  const { instance } = useMsal()

  const initApp = async () => {
    try {
      let account = instance.getActiveAccount()

      if (!account) {
        const accounts = instance.getAllAccounts()
        if (accounts.length > 0) {
          instance.setActiveAccount(accounts[0])
          account = accounts[0]
        }
      }

      if (account) {
        try {
          await instance.acquireTokenSilent({
            account,
            ...loginRequest,
          })
        } catch (error) {
          console.log('Silent token error:', error)
        }
      }
    } catch (error) {
      console.error('App error:', error)
    }
  }

  useEffect(() => {
    initApp()
  }, [])

  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#3D91B2',
        },
      }}
    >
      <AuthenticatedTemplate>
        <AuthenticatedRoutes />
      </AuthenticatedTemplate>
      <UnauthenticatedTemplate>
        <UnauthenticatedRoutes />
      </UnauthenticatedTemplate>
      <NotificationModal />
    </ConfigProvider>
  )
}

export default App
