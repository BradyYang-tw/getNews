import React, { useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router'
import { useMsal } from '@azure/msal-react'

import { loginRequest } from '@config/authConfig'
import { REDIRECT_URL } from '@typing/constants'
import { PATH } from '@routes/path'
import { Spin } from '@components/atoms'

const AuthAzureCallbackPage: React.FC = () => {
  const { t } = useTranslation()
  const { instance } = useMsal()
  const navigate = useNavigate()

  const redirectPage = (token: any) => {
    const path = sessionStorage.getItem(REDIRECT_URL)
    navigate(path || PATH.PROJECT_MANAGEMENT)
    sessionStorage.removeItem(REDIRECT_URL)
  }

  const initializeMsal = async () => {
    try {
      const response = await instance.handleRedirectPromise()
      if (response !== null) {
        instance.setActiveAccount(response.account)
        redirectPage(response)
      } else {
        const currentAccount = instance.getActiveAccount()

        if (!currentAccount) {
          const currToken = await instance.ssoSilent({
            ...loginRequest,
          })
          if (currToken) {
            instance.setActiveAccount(currToken?.account)
            redirectPage(currToken)
          }
        } else {
          const refreshToken = await instance.acquireTokenSilent({
            account: currentAccount,
            ...loginRequest,
          })

          if (refreshToken) {
            redirectPage(refreshToken)
          }
        }
      }
    } catch (error) {
      console.error('call back error:', error)
      await instance.loginRedirect({
        ...loginRequest,
        redirectUri: PATH.REDIRECT_PAGE,
      })
    }
  }

  useEffect(() => {
    initializeMsal()
  }, [])

  return (
    <div className="w-[100vw] h-[100vh] flex items-center justify-center">
      <Spin tip={t('LOGIN.SIGNING_IN')} size="large">
        <div className="p-[50px]" />
      </Spin>
    </div>
  )
}

export default AuthAzureCallbackPage
