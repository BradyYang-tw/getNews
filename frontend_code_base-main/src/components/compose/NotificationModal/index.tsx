import React from 'react'
import styles from './style.module.css'
import { CloseCircleFilled, ExclamationCircleFilled, InfoCircleFilled } from '@ant-design/icons'
import useNotificationStore from '@store/useNotificationStore'
import { Button, Modal } from '@components/atoms'

const NotificationModal: React.FC = () => {
  const { type, title, message, open, closeNotification } = useNotificationStore()

  const getIcon = () => {
    switch (type) {
      case 'warning':
        return <ExclamationCircleFilled className="!text-[#FAAD14]" /> // 黃色
      case 'error':
        return <CloseCircleFilled className="!text-[#FF4D4F]" /> // 紅色
      case 'info':
        return <InfoCircleFilled className="!text-[#1890FF]" /> // 藍色
      default:
        return <ExclamationCircleFilled className="!text-[#FAAD14]" /> // 默認為警告
    }
  }

  return (
    <Modal
      width={450}
      className={styles.modal}
      open={open}
      title={
        <div className="flex">
          <span className="!text-[20px] !mr-[10px]">{getIcon()}</span>
          <p className="m-0">{title}</p>
        </div>
      }
      closable={false}
      footer={[
        <Button key="ok-button" type="primary" onClick={() => closeNotification()}>
          OK
        </Button>,
      ]}
    >
      <p>{message}</p>
    </Modal>
  )
}

export default NotificationModal
