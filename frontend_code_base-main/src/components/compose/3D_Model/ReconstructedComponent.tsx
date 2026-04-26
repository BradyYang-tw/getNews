// src/components/ReconstructedComponent.tsx

import React, { useRef, useMemo, useEffect } from 'react'
import { useGLTF, useHelper } from '@react-three/drei'
import { Group, Object3D, BoxHelper, Box3, Vector3 } from 'three'

export interface BaseComponentData {
  name: string
  position: [number, number, number]
}

export interface ReconstructedComponentProps {
  data: BaseComponentData
  modelUrl: string
  onSelect: (name: string) => void
  isSelected: boolean
  isHovered?: boolean
  visible?: boolean
  onPointerOver?: (event: any) => void
  onPointerOut?: (event: any) => void
  onSizeReady: (name: string, size: Vector3) => void
}

const ReconstructedComponent: React.FC<ReconstructedComponentProps> = ({
  data,
  modelUrl,
  onSelect,
  isSelected,
  isHovered = false,
  visible = true,
  onPointerOver,
  onPointerOut,
  onSizeReady,
}) => {
  const groupRef = useRef<Group>(null)
  const { scene } = useGLTF(modelUrl)
  const clonedScene = useMemo(() => scene.clone(), [scene])

  useHelper(isSelected ? (groupRef as React.RefObject<Object3D>) : undefined, BoxHelper, '#3EECFF')
  useHelper(isHovered && !isSelected ? (groupRef as React.RefObject<Object3D>) : undefined, BoxHelper, '#FFFFFF')

  useEffect(() => {
    if (groupRef.current) {
      const box = new Box3().setFromObject(groupRef.current)
      if (!box.isEmpty()) {
        const size = box.getSize(new Vector3())
        onSizeReady(data.name, size)
      }
    }
  }, [data.name, onSizeReady, scene])

  const handleClick = (e: any) => {
    if (!visible) {
      return
    }
    e.stopPropagation()
    onSelect(data.name)
  }

  return (
    <group
      ref={groupRef}
      name={data.name}
      visible={visible}
      position={data.position}
      onClick={handleClick}
      onPointerOver={onPointerOver}
      onPointerOut={onPointerOut}
    >
      <primitive object={clonedScene} />
    </group>
  )
}

export default ReconstructedComponent
