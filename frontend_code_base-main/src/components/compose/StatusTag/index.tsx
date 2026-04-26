import React from 'react'

import { Tag } from '@components/atoms'

interface StatusTagProps {
  value: string
}

const STATUS_COLORS: Record<string, string> = {
  Completed: 'success',
  Ongoing: 'warning',
}

const StatusTag: React.FC<StatusTagProps> = ({ value }) => {
  const baseColor = STATUS_COLORS[value]

  if (!baseColor) {
    return <Tag>{value}</Tag>
  }

  return <Tag color={baseColor}>{value}</Tag>
}

export default StatusTag
