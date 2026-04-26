import React from 'react'
import { useMsal } from '@azure/msal-react'
import { useTranslation } from 'react-i18next'

import Logo from '@assets/icons/Logo'
import iconMicrosoft from '@assets/icons/icon_microsoft.png'
import { loginRequest } from '@config/authConfig'
import { PATH } from '@routes/path'
import { Button, Flex, Typography, Divider } from '@components/atoms'

const Login: React.FC = () => {
  const { t } = useTranslation()
  const { instance } = useMsal()

  const handleLogin = async () => {
    await instance.loginRedirect({
      ...loginRequest,
      redirectUri: PATH.REDIRECT_PAGE,
    })
  }

  return (
    <div className="bg-[url(../../src/assets/images/login_bg.png)] w-[100vw] h-[100vh] bg-cover bg-center flex flex-col p-[24px]">
      <div className="flex flex-col justify-center items-center flex-1">
        <Flex align="center">
          <Logo fillColor="none" />
          <Divider type="vertical" orientationMargin="20px" className="bg-gray-2 !h-[32px] w-[1px] !mx-[16px]" />
          <Typography.Text className="!text-white-1 !text-[24px] font-[700]">{t('LOGIN.SYSTEM_NAME')}</Typography.Text>
        </Flex>
        <Typography.Text className="!text-white-1 !text-[48px] font-[500] tracking-[.25em] mt-[16px] mb-[60px]">
          {t('LOGIN.TITLE')}
        </Typography.Text>
        <Button color="default" variant="solid" className="!text-[16px] !w-[231px] !h-[40px]" onClick={handleLogin}>
          <img src={iconMicrosoft} alt="microsoft" width="20px" />
          <p>Login with Microsoft</p>
        </Button>
      </div>
      <Typography.Text className="!text-gray-2 !text-[12px] !text-center tracking-[.15em]">
        Copyright © 2025 Wistron All rights reserved.
      </Typography.Text>
    </div>
  )
}

export default Login
