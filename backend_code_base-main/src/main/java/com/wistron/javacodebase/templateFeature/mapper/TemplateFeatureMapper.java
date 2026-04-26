package com.wistron.javacodebase.templateFeature.mapper;

import com.wistron.javacodebase.common.BaseMapper;
import com.wistron.javacodebase.templateFeature.vo.TemplateFeatureMappingVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface TemplateFeatureMapper extends BaseMapper<TemplateFeatureMappingVO> {

  @SelectProvider(type = TemplateFeatureSqlProvider.class, method = "findList")
  List<TemplateFeatureMappingVO> findList(@Param("size") int size, @Param("offset") int offset);

  @SelectProvider(type = TemplateFeatureSqlProvider.class, method = "countList")
  long countList();
}
