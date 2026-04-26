import React from 'react'
import { useTranslation } from 'react-i18next'
import { PlusOutlined } from '@ant-design/icons'

import { Breadcrumb, Flex, Button } from '@components/atoms'

const ProjectManagement: React.FC = () => {
  const { t } = useTranslation()

  return (
    <>
      <Breadcrumb
        className="!text-[20px] font-[700] !mb-[24px]"
        items={[{ title: t('PROJECT_MANAGEMENT.TITLE') }]}
      ></Breadcrumb>
      <Flex gap="middle" vertical className="!bg-white-1 min-h-[280px] !p-[24px]">
        <Flex align="center" justify="flex-end" gap="middle">
          <Button type="primary">
            <PlusOutlined />
            <span>Add</span>
          </Button>
        </Flex>
      </Flex>
    </>
  )
}

export default ProjectManagement
