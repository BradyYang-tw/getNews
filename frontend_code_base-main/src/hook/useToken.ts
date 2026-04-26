import { loginRequest } from '@config/authConfig'
import { msalInstance } from '@utils/auth'

const useToken = () => {
  const getAADToken = async () => {
    try {
      const activeAccount = msalInstance.getActiveAccount()
      if (activeAccount) {
        const resp = await msalInstance.acquireTokenSilent({
          ...loginRequest,
          account: activeAccount,
        })
        const token = `Bearer ${resp?.accessToken}`
        return token
      }
      return ''
    } catch (err) {
      console.log('err :>> ', err)
      return ''
    }
  }

  return {
    getAADToken,
  }
}

export default useToken
