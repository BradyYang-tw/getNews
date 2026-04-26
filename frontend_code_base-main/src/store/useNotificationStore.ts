import { create } from 'zustand'

export type NotificationType = 'error' | 'warning' | 'info'

export interface NotificationConfig {
  type: NotificationType
  title: string
  message: string
  onClose?: () => void
}

export interface NotificationState {
  type: NotificationType
  title: string
  message: string
  open: boolean
  onClose?: () => void
  openNotification: (config: NotificationConfig) => void
  closeNotification: () => void
}

const useNotificationStore = create<NotificationState>((set) => ({
  type: 'info',
  title: '',
  message: '',
  open: false,
  onClose: undefined,

  openNotification: (config) => {
    set(() => ({
      type: config.type,
      title: config.title,
      message: config.message,
      open: true,
      onClose: config.onClose,
    }))
  },

  closeNotification: () => {
    set((state) => {
      if (state.onClose) {
        state.onClose() // 執行 onClose 回調函數
      }
      return {
        open: false,
        title: '',
        message: '',
        onClose: undefined, // 清理 onClose
      }
    })
  },
}))

export default useNotificationStore
