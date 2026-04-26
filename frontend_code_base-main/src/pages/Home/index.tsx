import React from 'react'
import { useTranslation } from 'react-i18next'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { FileSearchOutlined } from '@ant-design/icons'

import Logo from '@assets/icons/Logo'
import { Flex, Layout, Typography, Menu, Divider } from '@components/atoms'
import AccountDropdown from '@components/compose/AccountDropdown'
import { APP_PREFIX } from '@routes/path'
import styles from './style.module.css'

const { Text } = Typography
const { Header, Content, Sider } = Layout

const Home: React.FC = () => {
  const { t } = useTranslation()

  const navigate = useNavigate()
  const location = useLocation()

  const selectedKey = location.pathname.split(`${APP_PREFIX}/`)[1] || 'project-management'
  // 例如 /ai-prediction => 'ai-prediction'

  return (
    <Layout className="w-[100vw] h-[100vh]">
      <Header className="flex text-white-1 h-[64px] !px-[12px] bg-blue-4!">
        <Flex align="center">
          <Logo fillColor="none" />
          <Divider type="vertical" orientationMargin="20px" className="!bg-white-1 !h-[32px] w-[1px] !mx-[16px]" />
          <Text className="!text-white-1 font-[700] !text-[24px]">{t('COMMON.SYSTEM_TITLE')}</Text>
        </Flex>

        <Flex align="center" flex={1} justify="end" gap={8}>
          <AccountDropdown />
        </Flex>
      </Header>
      <Layout>
        <Sider width={240} className="bg-[#203033]">
          <Menu
            mode="inline"
            selectedKeys={[selectedKey]}
            className={styles['custom-menu']}
            items={[
              {
                key: 'project-management',
                icon: <FileSearchOutlined className="!text-[24px]" />,
                label: t('PROJECT_MANAGEMENT.TITLE'),
                style: { color: '#FFFFFFA6' },
              },
            ]}
            onSelect={(e) => {
              navigate(`${APP_PREFIX}/${e.key}`)
            }}
          />
        </Sider>
        <Content className="text-left p-[24px] max-h-[calc(100vh - 64px)] overflow-auto">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}

export default Home
