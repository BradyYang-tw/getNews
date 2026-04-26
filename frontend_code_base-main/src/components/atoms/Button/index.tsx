import React from 'react'
import { Button, ButtonProps } from 'antd'

const CustomButton: React.FC<ButtonProps> = ({ ...props }) => {
  return <Button {...props} />
}

export default CustomButton
