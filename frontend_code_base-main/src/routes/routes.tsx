import { Navigate, Route, Routes } from 'react-router-dom'

import Home from '../pages/Home'
import ProjectManagement from '../pages/ProjectManagement'
import AuthAzureCallbackPage from '../pages/AuthAzureCallbackPage'
import Login from '../pages/Login'

import { PATH } from './path'

export const AuthenticatedRoutes = () => (
  <Routes>
    <Route element={<Home />}>
      <Route index element={<ProjectManagement />} />
      <Route path={PATH.PROJECT_MANAGEMENT} element={<ProjectManagement />} />
    </Route>
    <Route path={PATH.REDIRECT_PAGE} element={<AuthAzureCallbackPage />}></Route>
    <Route path={PATH.LOGIN} element={<Navigate to={PATH.PROJECT_MANAGEMENT} replace />} />
  </Routes>
)

export const UnauthenticatedRoutes = () => {
  return (
    <Routes>
      <Route path={PATH.LOGIN} element={<Login />}></Route>
      <Route path={PATH.REDIRECT_PAGE} element={<AuthAzureCallbackPage />}></Route>
      <Route path="*" element={<Navigate to={PATH.LOGIN} replace />} />
    </Routes>
  )
}
