import { Environment } from '@react-three/drei'

export const LightingSetup = () => {
  return (
    <>
      <Environment preset="apartment" />
      <ambientLight intensity={0.5} />
      <hemisphereLight intensity={0.5} color="#ffffff" groundColor="#b0b0b0" />
      <directionalLight position={[0, 10, 0]} intensity={1.5} castShadow />
      <directionalLight position={[10, 5, 10]} intensity={1.0} color="#c0e0ff" />
      <directionalLight position={[-10, 5, -10]} intensity={0.5} color="#c0e0ff" />
    </>
  )
}
