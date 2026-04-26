package com.wistron.javacodebase.templateFeature.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateFeatureListResponseVO {
  private String id;
  private String name;
  private String status;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
