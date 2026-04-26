import { CheckCircleFilled, EditOutlined, InfoCircleFilled } from '@ant-design/icons'
import { Flex, Button } from '@components/atoms'

const CustomCheckbox: React.FC<{ label: string; onEditClick?: () => void; checked: boolean }> = ({
  label,
  onEditClick,
  checked,
}) => {
  return (
    <Flex gap={4} justify="space-between" align="center">
      <Flex gap={4} align="center">
        {checked ? (
          <CheckCircleFilled className="!text-green-1 !text-[16px]" />
        ) : (
          <InfoCircleFilled className="!text-gray-1 !text-[16px]" />
        )}
        <span className="!text-[12px] !text-black-1">{label}</span>
      </Flex>
      {onEditClick && (
        <Button icon={<EditOutlined className="!text-blue-1" />} type="dashed" size="small" onClick={onEditClick} />
      )}
    </Flex>
  )
}
export default CustomCheckbox
