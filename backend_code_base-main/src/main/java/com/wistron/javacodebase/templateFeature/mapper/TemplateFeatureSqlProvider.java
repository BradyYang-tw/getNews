package com.wistron.javacodebase.templateFeature.mapper;

import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class TemplateFeatureSqlProvider {

  public String findList(Map<String, Object> params) {
    return new SQL() {{
      SELECT("id, name, status, create_time, update_time");
      FROM("topology.template_feature");
      WHERE("delete_time IS NULL");
      ORDER_BY("update_time DESC NULLS LAST");
    }}.toString() + " LIMIT #{size} OFFSET #{offset}";
  }

  public String countList() {
    return new SQL() {{
      SELECT("COUNT(1)");
      FROM("topology.template_feature");
      WHERE("delete_time IS NULL");
    }}.toString();
  }
}
