import React, { useEffect, useRef, useState } from 'react'

export interface OmniverseMessage {
  event_type: any
  payload: any
}

interface OmniverseViewerProps {
  message: OmniverseMessage | null
  ovPort: { signalingport: number; mediaport: number }
}

const OmniverseViewer: React.FC<OmniverseViewerProps> = ({ message, ovPort }) => {
  const ref = useRef<HTMLIFrameElement | null>(null)
  const [error, setError] = useState(false)
  const [isStreamReady, setIsStreamReady] = useState(false)
  const remoteRef = useRef<any>(null) // 使用 useRef 保存 remote 的引用

  // 初始化 remote
  useEffect(() => {
    const load = async () => {
      try {
        const remote = await import('ov_viewer/app')
        remoteRef.current = remote // 保存 remote 的引用
        setIsStreamReady(true)
      } catch (err) {
        console.warn('Ov viewer remote unavailable', err)
        setError(true)
      }
    }
    load()
    return () => {
      if (remoteRef.current && remoteRef.current.unmount) {
        try {
          ref.current = null
          remoteRef.current.unmount()
          setIsStreamReady(false)
          console.log('Micro frontend unmounted successfully')
        } catch (err) {
          console.warn('Failed to unmount micro frontend', err)
        }
      }
    }
  }, [])

  // 監聽 message 的變化
  useEffect(() => {
    if (isStreamReady && remoteRef.current && ref.current) {
      try {
        remoteRef.current.mount(ref.current, {
          signalingport: ovPort.signalingport,
          mediaport: ovPort.mediaport,
          message,
        }) // TODO：ov port 到時候要改成接 api 回傳的 port
      } catch (err) {
        console.warn('Failed to update message in remote.mount', err)
      }
    }
  }, [message, isStreamReady])

  if (error) {
    return <div className="w-[100vw] h-[100vh] flex justify-center items-center">OV 暫時無法使用</div>
  }

  return (
    <div className="w-[100%]">
      <div ref={ref} />
      {/* <iframe ref={ref} src="/wfe" width="100%" height="100%" title="Omniverse Viewer" /> */}
    </div>
  )
}

export default OmniverseViewer
