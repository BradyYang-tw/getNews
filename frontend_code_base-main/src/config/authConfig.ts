export const msalConfig = {
  auth: {
    clientId: import.meta.env.VITE_AZURE_APPLICATION_ID as string,
    authority: `https://login.microsoftonline.com/${import.meta.env.VITE_AZURE_TENANT_ID}`,
    navigateToLoginRequestUrl: false,
  },
  cache: {
    cacheLocation: 'localStorage', // This configures where your cache will be stored
    storeAuthStateInCookie: false, // Set this to "true" if you are having issues on IE11 or Edge
  },
}

export const loginRequest = {
  scopes: [`api://${import.meta.env.VITE_AZURE_APPLICATION_ID}/.default`],
}
