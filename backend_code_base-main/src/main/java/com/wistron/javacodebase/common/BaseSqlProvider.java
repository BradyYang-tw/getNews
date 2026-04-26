package com.wistron.javacodebase.common;

import com.wistron.javacodebase.annotation.mybatis.CanUpdateToNull;
import com.wistron.javacodebase.annotation.mybatis.UpdateOnConflict;
import com.wistron.javacodebase.common.utils.DBQueryUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Log4j2
public class BaseSqlProvider<T> {

  private static final String VALUE_OBJECT_TAIL = "MappingVO";
  private static final String JACOCO_DATA = "$jacocoData";
  private static final String SQL_FORMAT_JDBC_TYPE = ", jdbcType=";
  private static final String SQL_FORMAT_BEAN = "#{bean.";
  private static final String SQL_FORMAT_ON_CONFLICT = "ON CONFLICT (";
  private static final String CONDITION_FIELDS = "conditionFields";
  private static final String CONDITION_FIELD = "conditionField";

  private enum SQL_METHOD {
    INSERT_INTO,
    DELETE_FROM,
    SELECT,
    UPDATE,
    SELECT_COUNT
  }

  private enum SQL_OPERATOR {
    EQUAL,
    NON_EQUAL
  }

  /** INSERT */
  public String add(T bean) {

    SQL sql = getBaseSQLSelect(bean, SQL_METHOD.INSERT_INTO);
    List<Field> fields = DBQueryUtils.getFields(bean.getClass());
    for (Field field : fields) {
      if (!JACOCO_DATA.equals(field.getName())) {
        String column = field.getName();
        sql.VALUES(
          DBQueryUtils.humpToLine(column),
          String.format(
            "#{"
              + column
              + SQL_FORMAT_JDBC_TYPE
              + DBQueryUtils.classTypeToJdbcType(field)
              + "}"));
      }
    }
    log.debug("add SQL:", sql.toString());
    return sql.toString();
  }

  public String addAndReturnId(Map<String, Object> params) {

    T bean = (T) params.get("bean");
    String idField = (String) params.get("idField");

    SQL sql = getBaseSQLSelect(bean, SQL_METHOD.INSERT_INTO);
    List<Field> fields = DBQueryUtils.getFields(bean.getClass());
    for (Field field : fields) {
      if (!JACOCO_DATA.equals(field.getName())) {
        String column = field.getName();
        sql.VALUES(
          DBQueryUtils.humpToLine(column),
          String.format(
            SQL_FORMAT_BEAN
              + column
              + SQL_FORMAT_JDBC_TYPE
              + DBQueryUtils.classTypeToJdbcType(field)
              + "}"));
      }
    }

    StringBuilder sb = new StringBuilder(sql.toString());
    sb.append(" RETURNING ");
    sb.append(DBQueryUtils.humpToLine(idField));

    log.debug("addAndReturnId SQL:", sb.toString());
    return sb.toString();
  }

  public String addOnConflictDoNothing(T bean) {

    SQL sql = getBaseSQLSelect(bean, SQL_METHOD.INSERT_INTO);
    List<Field> fields = DBQueryUtils.getFields(bean.getClass());
    for (Field field : fields) {
      if (!JACOCO_DATA.equals(field.getName())) {
        String column = field.getName();
        sql.VALUES(
          DBQueryUtils.humpToLine(column),
          String.format(
            "#{"
              + column
              + SQL_FORMAT_JDBC_TYPE
              + DBQueryUtils.classTypeToJdbcType(field)
              + "}"));
      }
    }

    StringBuilder onConflictDoNothingSqlStr = new StringBuilder(sql.toString());
    List<Field> primaryKeyField = DBQueryUtils.getPKFields(bean.getClass());
    onConflictDoNothingSqlStr.append(SQL_FORMAT_ON_CONFLICT);
    onConflictDoNothingSqlStr.append(
      primaryKeyField.stream()
        .map(pkField -> DBQueryUtils.humpToLine(pkField.getName()))
        .collect(Collectors.joining(",")));
    onConflictDoNothingSqlStr.substring(onConflictDoNothingSqlStr.length() - 1);
    onConflictDoNothingSqlStr.append(") DO NOTHING");

    log.debug("addOnConflictDoNothing SQL:", onConflictDoNothingSqlStr.toString());
    return onConflictDoNothingSqlStr.toString();
  }

  public String addOnConflictDoUpdate(T bean) {
    String onConflictDoUpdateSqlStr = this.getAddOnConflictDoUpdateSql(bean, null);
    log.debug("onConflictDoUpdateSqlStr SQL:", onConflictDoUpdateSqlStr);
    return onConflictDoUpdateSqlStr;
  }

  private String getAddOnConflictDoUpdateSql(T bean, String alias) {
    SQL sql = getBaseSQLSelect(bean, SQL_METHOD.INSERT_INTO);
    List<Field> fields = DBQueryUtils.getFields(bean.getClass());
    String aliasStr = alias == null ? "" : alias + ".";
    for (Field field : fields) {
      if (!JACOCO_DATA.equals(field.getName())) {
        String column = field.getName();
        sql.VALUES(
          DBQueryUtils.humpToLine(column),
          String.format(
            "#{"
              + aliasStr
              + column
              + SQL_FORMAT_JDBC_TYPE
              + DBQueryUtils.classTypeToJdbcType(field)
              + "}"));
      }
    }

    StringBuilder onConflictDoUpdateSqlStr = new StringBuilder(sql.toString());
    List<Field> primaryKeyField = DBQueryUtils.getPKFields(bean.getClass());
    onConflictDoUpdateSqlStr.append(SQL_FORMAT_ON_CONFLICT);
    onConflictDoUpdateSqlStr.append(
      primaryKeyField.stream()
        .map(pkField -> DBQueryUtils.humpToLine(pkField.getName()))
        .collect(Collectors.joining(",")));
    onConflictDoUpdateSqlStr.substring(onConflictDoUpdateSqlStr.length() - 1);
    onConflictDoUpdateSqlStr.append(") DO UPDATE SET ");

    /** 所有欄位中扣除PrimaryKey的，視為要被更新的欄位 */
    List<Field> updateFields = fields;
    updateFields.removeAll(primaryKeyField);

    List<String> updateFieldValues = new ArrayList<>();
    updateFields.forEach(
      field -> {
        if (!JACOCO_DATA.equals(field.getName())) {
          field.setAccessible(true);
          String column = field.getName();
          PropertyDescriptor ps = BeanUtils.getPropertyDescriptor(bean.getClass(), column);
          Object value = ReflectionUtils.invokeMethod(ps.getReadMethod(), bean);
          /**
           * 若MappingVO中的filed value為null或是欄位名稱為id或createTime的，則不會update
           * 若欄位名稱為delete_time或delete_date或有@CanUpdateToNull的話，則不判斷欄位值是否為null
           */
          CanUpdateToNull canUpdateToNullItem = field.getAnnotation(CanUpdateToNull.class);
          UpdateOnConflict updateOnConflictItem = field.getAnnotation(UpdateOnConflict.class);
          if ((StringUtils.equals(column, "deleteTime") || StringUtils.equals(column, "deleteDate"))
            || (value != null && !StringUtils.equals(column, "id") && !StringUtils.equals(column, "createTime"))
            || (canUpdateToNullItem != null)
            || (value != null && updateOnConflictItem != null)) {
            updateFieldValues.add(
              DBQueryUtils.humpToLine(column)
                + "="
                + String.format(
                "#{"
                  + aliasStr
                  + column
                  + ",jdbcType="
                  + DBQueryUtils.classTypeToJdbcType(field)
                  + "}"));
          }
        }
      });
    onConflictDoUpdateSqlStr.append(String.join(",", updateFieldValues));
    return onConflictDoUpdateSqlStr.toString();
  }

  public String addOnConflictDoUpdateReturnStrId(Map<String, Object> params) {
    T bean = (T) params.get("bean");
    String idField = (String) params.get("idField");
    String updateSql = this.getAddOnConflictDoUpdateSql(bean, "bean");
    StringBuilder sb = new StringBuilder(updateSql);
    sb.append(" RETURNING ");
    sb.append(DBQueryUtils.humpToLine(idField));
    log.debug("addOnConflictDoUpdateReturnStrId SQL:", sb.toString());
    return sb.toString();
  }

  public String batchInsert(@Param("beans") Map<String, Object> params) {
    List<T> beans = (List<T>) params.get("beans");
    if (CollectionUtils.isEmpty(beans)) return "";
    T bean = beans.get(0);
    SQL baseSql = getBaseSQLSelect(bean, SQL_METHOD.INSERT_INTO);
    List<Field> fields = DBQueryUtils.getFields(bean.getClass());
    StringBuffer sql = new StringBuffer(baseSql.toString());
    sql.append(" (");
    for (Field field : fields) {
      // the field "$jacocoData" will be generator when test.
      if (!JACOCO_DATA.equals(field.getName())) {
        sql.append(DBQueryUtils.humpToLine(field.getName())).append(",");
      }
      //            	sql.append(fields.stream().map(field ->
      // DBQueryUtils.humpToLine(field.getName()))
      //                    .collect(Collectors.joining(",")));
    }
    sql.delete(sql.length() - 1, sql.length());
    sql.append(") ");
    sql.append("VALUES");

    StringBuilder msgSb = new StringBuilder();
    String MSG_SB_FORMAT = "#'{'beans[{0}].%s}";
    msgSb.append(" (");
    for (Field field : fields) {
      // the field "$jacocoData" will be generator when test.
      if (!JACOCO_DATA.equals(field.getName())) {
        msgSb.append(String.format(MSG_SB_FORMAT, field.getName())).append(",");
      }
    }
    //            msgSb.append(fields.stream().map(field -> String.format(MSG_SB_FORMAT,
    // field.getName()))
    //                    .collect(Collectors.joining(",")));
    msgSb.delete(msgSb.length() - 1, msgSb.length());
    msgSb.append(") ");

    MessageFormat mf = new MessageFormat(msgSb.toString());

    for (int i = 0; i < beans.size(); i++) {
      sql.append(mf.format(new Object[] {String.valueOf(i)}));
      if (i < beans.size() - 1) {
        sql.append(",");
      }
    }
    log.debug("batchInsert SQL:", sql.toString());
    return sql.toString();
  }

  public String batchInsertOnConflictNothingToDo(@Param("beans") Map<String, Object> params) {
    List<T> beans = (List<T>) params.get("beans");
    if (CollectionUtils.isNotEmpty(beans)) {
      T bean = beans.get(0);
      StringBuilder onConflictDoNothingSqlStr = new StringBuilder(batchInsert(params));
      List<Field> primaryKeyField = DBQueryUtils.getPKFields(bean.getClass());
      onConflictDoNothingSqlStr.append(SQL_FORMAT_ON_CONFLICT);
      onConflictDoNothingSqlStr.append(
        primaryKeyField.stream()
          .map(pkField -> DBQueryUtils.humpToLine(pkField.getName()))
          .collect(Collectors.joining(",")));
      onConflictDoNothingSqlStr.substring(onConflictDoNothingSqlStr.length() - 1);
      onConflictDoNothingSqlStr.append(") DO NOTHING");

      log.debug("batchInsertOnConflictDoNothing SQL:", onConflictDoNothingSqlStr.toString());
      return onConflictDoNothingSqlStr.toString();
    }
    return "";
  }

  /**************************************************************************************************/

  /** SELECT */
  public String findAll(Class<? extends T> clazz) {
    SQL sql = new SQL();
    String tableName = clazz.getSimpleName();
    String realTableName =
      DBQueryUtils.humpToLine(tableName.replace(VALUE_OBJECT_TAIL, "")).substring(1);
    sql.SELECT("*").FROM(realTableName);
    log.debug("findAll SQL:", sql.toString());
    return sql.toString();
  }

  public String getOneByPk(T bean) {
    return findAllByPk(bean);
  }

  public String findAllBy(Map<String, Object> params) {

    T bean = (T) params.get("bean");
    String[] conditionFields = (String[]) params.get(CONDITION_FIELDS);

    String sqlStr =
      findBySqlGenerator(bean, conditionFields, SQL_OPERATOR.EQUAL, SQL_METHOD.SELECT);

    log.debug("findAllBy SQL:", sqlStr);
    return sqlStr;
  }

  public String findAllCountBy(Map<String, Object> params) {

    T bean = (T) params.get("bean");
    String[] conditionFields = (String[]) params.get(CONDITION_FIELDS);

    String sqlStr =
      findBySqlGenerator(bean, conditionFields, SQL_OPERATOR.EQUAL, SQL_METHOD.SELECT_COUNT);

    log.debug("findAllCountBy SQL:", sqlStr);
    return sqlStr;
  }

  public String findAllNonEqualBy(Map<String, Object> params) {

    T bean = (T) params.get("bean");
    String[] conditionFields = (String[]) params.get(CONDITION_FIELDS);

    String sqlStr =
      findBySqlGenerator(bean, conditionFields, SQL_OPERATOR.NON_EQUAL, SQL_METHOD.SELECT);

    log.debug("findAllNonEqualBy SQL:", sqlStr);
    return sqlStr;
  }

  public String findAllByPk(T bean) {

    SQL sql = getBaseSQLSelect(bean, SQL_METHOD.SELECT);

    List<Field> primaryKeyField = DBQueryUtils.getPKFields(bean.getClass());
    if (!primaryKeyField.isEmpty()) {
      for (Field pkField : primaryKeyField) {
        String column = pkField.getName();
        pkField.setAccessible(true);
        sql.WHERE(
          DBQueryUtils.humpToLine(column) + "=" + String.format("#{" + pkField.getName() + "}"));
      }
    } else {
      sql.WHERE("1=2");
      throw new RuntimeException("PrimaryKey Not Found");
    }
    if (StringUtils.isNotEmpty(sql.toString())) {
      log.debug("findAllByPk SQL:", sql.toString());
    }
    return sql.toString();
  }

  public String findOneBy(Map<String, Object> params) {
    T bean = (T) params.get("bean");
    String[] conditionFields = (String[]) params.get(CONDITION_FIELDS);

    String sqlStr =
      findBySqlGenerator(bean, conditionFields, SQL_OPERATOR.EQUAL, SQL_METHOD.SELECT);

    log.debug("findAllBy SQL:", sqlStr);
    return sqlStr;
  }

  private String findBySqlGenerator(
    T bean, String[] conditionFields, SQL_OPERATOR sqlOperator, SQL_METHOD sqlMethod) {
    SQL sql = getBaseSQLSelect(bean, sqlMethod);
    List<Field> fields = DBQueryUtils.getFields(bean.getClass());
    List<String> conditionFieldList = Arrays.asList(conditionFields);
    for (Field field : fields) {
      String column = field.getName();
      if (conditionFieldList.contains(column)) {
        String operator = "=";
        switch (sqlOperator) {
          case EQUAL:
            operator = "=";
            break;
          case NON_EQUAL:
            operator = "!=";
            break;
        }
        sql.WHERE(
          DBQueryUtils.humpToLine(column)
            + operator
            + String.format(SQL_FORMAT_BEAN + field.getName() + "}"));
      }
    }
    return sql.toString();
  }

  /**************************************************************************************************/

  public String updateBy(Map<String, Object> params) {

    T bean = (T) params.get("bean");
    Class clazz = bean.getClass();

    SQL sql = getBaseSQLSelect(bean, SQL_METHOD.UPDATE);

    String[] conditionFields = (String[]) params.get(CONDITION_FIELDS);
    List<String> conditionFieldList = Arrays.asList(conditionFields);

    List<Field> fields = DBQueryUtils.getFields(clazz);

    /** 不存在於conditionFields欄位者，視為要被更新的欄位 */
    List<Field> updateFields =
      fields.stream()
        .filter(field -> !conditionFieldList.contains(field.getName()))
        .collect(Collectors.toList());

    AtomicReference<Boolean> doUpdate = new AtomicReference<>(false);
    updateFields.forEach(
      field -> {
        if (!JACOCO_DATA.equals(field.getName())) {
          field.setAccessible(true);
          String column = field.getName();
          PropertyDescriptor ps = BeanUtils.getPropertyDescriptor(bean.getClass(), column);
          Object value = ReflectionUtils.invokeMethod(ps.getReadMethod(), bean);
          if (value != null || field.getAnnotation(CanUpdateToNull.class) != null) {
            doUpdate.set(true);
          }
        }
      });

    /** 如果所有需更新欄位皆為null，則不執行SQL Update */
    if (!doUpdate.get()) {
      return "";
    }
    updateFields.forEach(
      field -> {
        if (!JACOCO_DATA.equals(field.getName())) {
          field.setAccessible(true);
          String column = field.getName();
          PropertyDescriptor ps = BeanUtils.getPropertyDescriptor(bean.getClass(), column);
          Object value = ReflectionUtils.invokeMethod(ps.getReadMethod(), bean);
          /** 若MappingVO中的filed value為null，則不會update */
          if (value != null || field.getAnnotation(CanUpdateToNull.class) != null) {
            sql.SET(
              DBQueryUtils.humpToLine(column)
                + "="
                + String.format(
                SQL_FORMAT_BEAN
                  + column
                  + ",jdbcType="
                  + DBQueryUtils.classTypeToJdbcType(field)
                  + "}"));
          }
        }
      });

    conditionFieldList.forEach(
      conditionField ->
        sql.WHERE(
          DBQueryUtils.humpToLine(conditionField)
            + "="
            + String.format(SQL_FORMAT_BEAN + conditionField + "}")));

    log.debug("update SQL: ", sql.toString());
    return sql.toString();
  }

  public String deleteBy(Map<String, Object> params) {

    T bean = (T) params.get("bean");
    Class clazz = bean.getClass();

    SQL sql = getBaseSQLSelect(bean, SQL_METHOD.DELETE_FROM);

    String[] conditionFields = (String[]) params.get(CONDITION_FIELDS);
    List<String> conditionFieldList = Arrays.asList(conditionFields);

    conditionFieldList.forEach(
      conditionField ->
        sql.WHERE(
          DBQueryUtils.humpToLine(conditionField)
            + "="
            + String.format(SQL_FORMAT_BEAN + conditionField + "}")));

    log.debug("delete SQL:", sql.toString());
    return sql.toString();
  }

  public String deleteByWithIn(Map<String, Object> params) {

    Class clazz = (Class) params.get("clazz");
    SQL sql = getBaseSQLSelect(clazz, SQL_METHOD.DELETE_FROM);

    List<String> ids = (List<String>) params.get("ids");

    String conditionField = (String) params.get(CONDITION_FIELD);

    List<String> idList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(ids)) {
      MessageFormat mf = new MessageFormat("#'{'ids[{0}]}");
      for (int i = 0; i < ids.size(); i++) {
        idList.add(mf.format(new Object[] {String.valueOf(i)}));
      }
    }

    sql.WHERE(
      DBQueryUtils.humpToLine(conditionField) + " in (" + StringUtils.join(idList, ",") + ")");

    log.debug("delete SQL:", sql.toString());
    return sql.toString();
  }

  private SQL getBaseSQLSelect(T bean, SQL_METHOD method) {
    return getBaseSQLSelect(bean.getClass(), method);
  }

  private SQL getBaseSQLSelect(Class clazz, SQL_METHOD method) {
    SQL sql = new SQL();
    String tableName = clazz.getSimpleName();
    String realTableName =
      DBQueryUtils.humpToLine(tableName.replace(VALUE_OBJECT_TAIL, "")).substring(1);
    switch (method) {
      case INSERT_INTO:
        sql.INSERT_INTO(realTableName);
        break;
      case DELETE_FROM:
        sql.DELETE_FROM(realTableName);
        break;
      case SELECT:
        sql.SELECT("*").FROM(realTableName);
        break;
      case UPDATE:
        sql.UPDATE(realTableName);
        break;
      case SELECT_COUNT:
        sql.SELECT("count(*) as totalCount").FROM(realTableName);
        break;
    }
    return sql;
  }

  public String findAllByStrList(
    Class<? extends T> clazz, String targetField, List<String> strIdList) {

    List<Field> fields = DBQueryUtils.getFields(clazz);

    String targetFieldName = null;

    if (!fields.isEmpty()) {
      for (Field f : fields) {
        f.setAccessible(true);
        String fType = f.getType().getSimpleName();
        String fName = f.getName();
        if (StringUtils.compare(fType, "String") == 0
          && StringUtils.compare(fName, targetField) == 0) {
          targetFieldName = fName;
          break;
        }
      }
    }
    if (targetFieldName == null) {
      throw new RuntimeException(targetField + " Not Found in Class");
    }
    SQL sql = new SQL();
    String tableName = clazz.getSimpleName();
    String realTableName =
      DBQueryUtils.humpToLine(tableName.replace(VALUE_OBJECT_TAIL, "")).substring(1);
    sql.SELECT("*").FROM(realTableName);
    String ids = "(" + DBQueryUtils.convertStringListToSqlLiteral(strIdList) + ")";
    sql.WHERE(DBQueryUtils.humpToLine(targetFieldName) + " IN " + ids);

    return sql.toString();
  }
}
