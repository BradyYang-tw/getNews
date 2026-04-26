import { Avatar } from 'antd'

const CustomAvatar = (props: React.ComponentProps<typeof Avatar>) => {
  return <Avatar className="!text-[#f56a00] !bg-white-1" {...props} />
}

export default CustomAvatar
