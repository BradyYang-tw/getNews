import React from 'react'
import { useTranslation } from 'react-i18next'
import type { MenuProps } from 'antd'
import { CaretDownFilled, CheckOutlined, GlobalOutlined } from '@ant-design/icons'
import { useMsal } from '@azure/msal-react'

import { Avatar, Dropdown, Space } from '@components/atoms'
import { PATH } from '@routes/path'
import LogoutIcon from '@assets/icons/LogoutIcon'

const AccountDropdown: React.FC = () => {
  const { i18n } = useTranslation()
  const { accounts, instance } = useMsal()

  const items: MenuProps['items'] = [
    {
      key: 'language',
      label: 'Language',
      icon: <GlobalOutlined className="!text-[16px]" />,
      children: [
        {
          key: 'Chinese',
          label: (
            <div className="flex">
              <div className="w-[20px]">{i18n.language === 'zh' && <CheckOutlined className="!text-[#3D91B2]" />}</div>
              中文
            </div>
          ),
        },
        {
          key: 'English',
          label: (
            <div className="flex">
              <div className="w-[20px]">{i18n.language === 'en' && <CheckOutlined className="!text-[#3D91B2]" />}</div>
              English
            </div>
          ),
        },
      ],
    },
    {
      key: 'logout',
      label: 'Logout',
      icon: <LogoutIcon />,
    },
  ]

  const onClick: MenuProps['onClick'] = ({ key }) => {
    if (key === 'logout') {
      instance
        .logoutRedirect({
          postLogoutRedirectUri: PATH.LOGIN,
        })
        .catch((err) => {
          console.error('handleLogout err', err)
        })
    } else if (key === 'Chinese') {
      i18n.changeLanguage('zh')
    } else if (key === 'English') {
      i18n.changeLanguage('en')
    }
  }

  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
      <Avatar>{accounts[0]?.name?.charAt(0) || ''}</Avatar>
      <Dropdown menu={{ items, onClick }} trigger={['click']} placement="topLeft">
        <a onClick={(e) => e.preventDefault()}>
          <Space style={{ color: 'white' }}>
            {accounts[0]?.name || ''}
            <CaretDownFilled />
          </Space>
        </a>
      </Dropdown>
    </div>
  )
}

export default AccountDropdown
