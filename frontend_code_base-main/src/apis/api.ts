import axios, {
  AxiosInstance,
  AxiosResponse,
  AxiosError,
  InternalAxiosRequestConfig, // 使用 InternalAxiosRequestConfig 以符合較新 Axios 版本
} from 'axios'
import i18next from 'i18next'
import { ERROR_MESSAGES, REDIRECT_URL } from '../typing/constants'
import { PATH } from '../routes/path'
import { API_PREFIX } from './path'
import useToken from '@hook/useToken'
import useNotificationStore from '@store/useNotificationStore'

// --- Configuration ---

// API 相關設定
const apiConfig = {
  baseURL: `${import.meta.env.VITE_API_BASE_URL}${API_PREFIX}`,
  timeout: 60000, // 請求超時時間 (60 秒)
}

console.log(`[API Client] Base URL configured to: ${apiConfig.baseURL}`)

// --- Axios Instance Creation ---

// 建立並設定 Axios 實例
const axiosInstance: AxiosInstance = axios.create({
  baseURL: apiConfig.baseURL,
  timeout: apiConfig.timeout,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})

// --- Interceptors ---

// Request Interceptor: 在每個請求發送前執行
axiosInstance.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    console.log('API Path:', config.url)

    if (config.data) {
      console.log('API Request:', config.data)
    }

    const token = await useToken().getAADToken()

    if (token) {
      // 如果存在 Token，將其附加到 Authorization 標頭
      config.headers.Authorization = token
    } else {
      console.log('[API Client] No token found, sending request without Authorization header.')
      return Promise.reject(new Error(ERROR_MESSAGES.NO_AUTHENTICATION))
    }

    // 可以在這裡添加其他通用請求邏輯，例如：
    // - 添加通用的追蹤 ID
    // - 修改請求資料格式等
    return config
  },
  (error: AxiosError) => {
    // 處理請求設定階段的錯誤
    console.error('[API Client] Request Interceptor Error:', error)
    return Promise.reject(error) // 將錯誤繼續傳遞下去
  },
)

// Response Interceptor: 在收到回應後執行
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    // 對於狀態碼在 2xx 範圍內的回應
    console.log('API Response:', response.data)

    // 通常直接返回 response，讓呼叫端可以訪問 status, headers 等資訊
    // 如果你總是只需要 data，可以解除下面這行的註解，但會失去其他回應資訊
    // return response.data;
    return response
  },
  (error: AxiosError) => {
    // 對於狀態碼超出 2xx 範圍的回應
    // console.error('[API Client] Response Interceptor - Error:', error); // Debugging

    // 使用統一定義的錯誤處理函數進行日誌記錄和分析
    handleApiError(error, 'Response Interceptor')

    // 根據 HTTP 狀態碼處理特定邏輯
    if (error.response?.status === 401 || error.message === ERROR_MESSAGES.NO_AUTHENTICATION) {
      // 未授權：可能是 Token 過期或無效
      console.warn('[API Client] Unauthorized (401). Token might be invalid or expired.')

      useNotificationStore.getState().openNotification({
        type: 'warning',
        title: i18next.t('COMMON.SESSION_EXPIRED_TITLE'),
        message: i18next.t('COMMON.SESSION_EXPIRED_CONTENT'),
        onClose: () => {
          sessionStorage.setItem(REDIRECT_URL, `${location?.pathname}${location?.search}`)
          window.location.href = window.location.origin + PATH.REDIRECT_PAGE
        },
      })
    }
    // 可以為其他常見錯誤碼 (如 403 Forbidden, 500 Internal Server Error) 添加處理

    // *** 關鍵 ***: 必須將錯誤 reject 出去，
    // 這樣 `catch` 區塊或 `.catch()` 方法才能在呼叫 API 的地方捕獲到錯誤，
    // 以便執行 UI 更新 (例如顯示錯誤訊息、設定 loading 狀態為 false)。
    return Promise.reject(error)
  },
)

// --- Error Handling Helper ---

/**
 * 統一處理和記錄 API 錯誤。
 * @param {unknown} error - 捕獲到的錯誤物件，預期是 AxiosError。
 * @param {string} [context='API Call'] - 錯誤發生的上下文描述 (例如，攔截器名稱或函數名稱)。
 */
const handleApiError = (error: unknown, context: string = 'API Call'): void => {
  if (axios.isAxiosError(error)) {
    // 處理 Axios 錯誤
    const axiosError = error as AxiosError // 類型斷言，因為 isAxiosError 已確認
    const { message, response, request, config } = axiosError

    const requestInfo = config ? `[${config.method?.toUpperCase()}] ${config.url}` : 'N/A'

    let logDetails: Record<string, any> = {
      context: context,
      request: requestInfo,
      message: message,
    }

    if (response) {
      // 伺服器回應了錯誤狀態碼 (非 2xx)
      logDetails = {
        ...logDetails,
        status: response.status,
        responseData: response.data, // 包含伺服器返回的錯誤詳情
      }
      console.error(`[API Client] Response Error (${response.status}) in ${context}:`, logDetails)
    } else if (request) {
      // 請求已發出，但沒有收到回應 (網路問題、超時等)
      logDetails = {
        ...logDetails,
        details: 'No response received from server (Network Error or Timeout).',
      }
      console.error(`[API Client] Network/Timeout Error in ${context}:`, logDetails)
    } else {
      // 設定請求時發生錯誤 (不太常見，但可能發生)
      logDetails = {
        ...logDetails,
        details: 'Error occurred during request setup.',
      }
      console.error(`[API Client] Request Setup Error in ${context}:`, logDetails)
    }

    // 考慮是否要記錄完整的 config，它可能包含敏感資訊 (如請求體)
    // console.error('[API Client] Full Axios Config (potential secrets):', config);

    useNotificationStore.getState().openNotification({
      type: 'error',
      title: 'error',
      message: logDetails?.message || i18next.t('COMMON.SYSTEM_ERROR'),
    })
  } else {
    // 處理非 Axios 錯誤 (例如，處理回應時的程式碼錯誤)
    console.error(`[API Client] Non-Axios Error in ${context}:`, error)
    useNotificationStore.getState().openNotification({
      type: 'error',
      title: 'error',
      message: i18next.t('COMMON.SYSTEM_ERROR'),
    })
  }
  // 這個輔助函數主要負責記錄和分析錯誤，不負責重新拋出，
  // 因為攔截器或呼叫端的 catch 區塊會處理 Promise 的 reject。
}

export default axiosInstance
export { apiConfig, handleApiError }
export { axiosInstance as apiClient } // 將 axiosInstance 匯出為 apiClient
