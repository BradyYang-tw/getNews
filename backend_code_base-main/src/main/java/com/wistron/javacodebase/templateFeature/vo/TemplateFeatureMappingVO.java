package com.wistron.javacodebase.templateFeature.vo;

import com.wistron.javacodebase.annotation.mybatis.PrimaryKey;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateFeatureMappingVO {
  @PrimaryKey private UUID id;
  private String name;
  private String status;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
  private LocalDateTime deleteTime;
}
