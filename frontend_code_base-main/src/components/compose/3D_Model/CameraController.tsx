import React, { useEffect } from 'react'
import { Vector3 } from 'three'
import { useThree } from '@react-three/fiber'
import type { OrbitControls as OrbitControlsImpl } from 'three-stdlib'

interface CameraControllerProps {
  view?: string
  center: Vector3
  size: Vector3
}

const CameraController: React.FC<CameraControllerProps> = ({ view = 'frontView', center, size }) => {
  const { camera, controls } = useThree()

  useEffect(() => {
    if (!controls || !center || !size || size.length() === 0) return

    const orbitControls = controls as OrbitControlsImpl
    const maxDim = Math.max(size.x, size.y, size.z)

    // 將攝影機的 "up" 向量重設為 Y 軸向上，這是絕大多數視角的標準
    camera.up.set(0, 1, 0)

    const newPosition = new Vector3()

    switch (view) {
      case 'topView':
        // 從 Y 軸正向上方往下看
        newPosition.set(center.x, center.y + maxDim, center.z)
        // 為避免萬向鎖，指定攝影機頂部朝向 Z 軸負方向
        camera.up.set(0, 0, -1)
        break

      case 'rightView':
        // 從 X 軸正向（右方）往左看
        newPosition.set(center.x + maxDim, center.y, center.z)
        break

      case 'frontView':
        // 從 Z 軸正向（前方）往後看
        newPosition.set(center.x, center.y, center.z + maxDim)
        break

      case 'perspectiveView':
        // 從 X, Y, Z 均為正的象限看向中心，產生標準立體圖
        const dist = maxDim * 0.75
        newPosition.set(center.x + dist, center.y + dist, center.z + dist)
        break

      default:
        return
    }

    camera.position.copy(newPosition)
    orbitControls.target.copy(center)
    orbitControls.update()

    return () => {
      if (controls) {
        camera.up.set(0, 1, 0)
      }
    }
  }, [view, center, size, camera, controls])

  return null
}

export default CameraController
