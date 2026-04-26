import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import enUS from './en-US.json'
import zhTW from './zh-TW.json'

i18n.use(initReactI18next).init({
  resources: {
    en: {
      translation: enUS,
    },
    zh: {
      translation: zhTW,
    },
  },
  lng: 'zh',
  fallbackLng: 'zh',
  interpolation: {
    escapeValue: false,
  },
})

export default i18n
