package com.wistron.javacodebase.templateFeature;

import com.wistron.javacodebase.common.dto.PageResponse;
import com.wistron.javacodebase.templateFeature.mapper.TemplateFeatureMapper;
import com.wistron.javacodebase.templateFeature.vo.TemplateFeatureListResponseVO;
import com.wistron.javacodebase.templateFeature.vo.TemplateFeatureMappingVO;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TemplateFeatureService {

  private final TemplateFeatureMapper templateFeatureMapper;

  public TemplateFeatureService(TemplateFeatureMapper templateFeatureMapper) {
    this.templateFeatureMapper = templateFeatureMapper;
  }

  public PageResponse<TemplateFeatureListResponseVO> list(int page, int size) {
    int pageIndex = Math.max(page - 1, 0);
    int offset = pageIndex * size;

    List<TemplateFeatureMappingVO> rows = templateFeatureMapper.findList(size, offset);
    long totalElements = templateFeatureMapper.countList();
    int totalPages = (int) Math.ceil((double) totalElements / size);

    List<TemplateFeatureListResponseVO> content = rows.stream().map(this::toResponse).toList();
    return new PageResponse<>(content, page, size, totalElements, totalPages);
  }

  private TemplateFeatureListResponseVO toResponse(TemplateFeatureMappingVO row) {
    return TemplateFeatureListResponseVO.builder()
        .id(row.getId() == null ? null : row.getId().toString())
        .name(row.getName())
        .status(row.getStatus())
        .createTime(row.getCreateTime())
        .updateTime(row.getUpdateTime())
        .build();
  }
}
