package com.wistron.javacodebase.common.utils;


import com.wistron.javacodebase.annotation.mybatis.DateFormat;
import com.wistron.javacodebase.annotation.mybatis.Exclude;
import com.wistron.javacodebase.annotation.mybatis.PrimaryKey;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

public class DBQueryUtils {

  private static Pattern linePattern = Pattern.compile("_(\\w)");

  public static String lineToHump(String str) {
    str = str.toLowerCase();
    Matcher matcher = linePattern.matcher(str);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  private static Pattern humpPattern = Pattern.compile("[A-Z0-9]");

  public static String humpToLine(String str) {
    Matcher matcher = humpPattern.matcher(str);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  public static String classTypeToJdbcType(Field field) {
    Class clazz = field.getType();
    if (clazz.getName().contains("Date")) {
      return JdbcType.TIMESTAMP.name();
    }
    if (clazz.getName().contains("String")) {
      // check is dateformat or not
      boolean isDate = isFieldHasAnnotation(field, DateFormat.class);
      if (isDate) {
        return JdbcType.DATE.name();
      } else {
        return JdbcType.VARCHAR.name();
      }
    }
    if (clazz.getName().contains("Integer")) {
      return JdbcType.INTEGER.name();
    }
    if (clazz.getName().contains("Float")) {
      return JdbcType.FLOAT.name();
    }
    if (clazz.getName().contains("Double")) {
      return JdbcType.BIGINT.name();
    }
    if (clazz.getName().contains("Boolean")) {
      return JdbcType.BOOLEAN.name();
    }
    if (clazz.getName().contains("Long")) {
      return JdbcType.BIGINT.name();
    }
    if (clazz.getName().contains("BigDecimal")) {
      return JdbcType.NUMERIC.name();
    }
    /** default data type */
    return JdbcType.VARCHAR.name();
  }

  public static Boolean isFieldHasAnnotation(Field field, Class annotationClazz) {
    return field.getAnnotation(annotationClazz) != null;
  }

  public static List<Field> getPKFields(Class clazz) {
    List<Field> primaryKeyField = new ArrayList<>();
    List<Field> fields = getFields(clazz);
    for (Field field : fields) {
      PrimaryKey key = field.getAnnotation(PrimaryKey.class);
      if (key != null) {
        primaryKeyField.add(field);
      }
    }
    return primaryKeyField;
  }

  public static List<Field> getFields(Class clazz) {
    List<Field> fieldList = new ArrayList<>();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      Exclude key = field.getAnnotation(Exclude.class);
      if (key == null) {
        fieldList.add(field);
      }
    }
    return fieldList;
  }

  public static List<String> msgFormatList(String listName, List<String> list) {
    List<String> mfList = new ArrayList<String>();
    if (list != null && list.size() > 0) {
      MessageFormat mf = new MessageFormat("#'{'" + listName + "[{0}]}");
      for (int i = 0; i < list.size(); i++) {
        mfList.add(mf.format(new Object[] {String.valueOf(i)}));
      }
    }
    return mfList;
  }

  public static String convertStringListToSqlLiteral(List<String> strings) {
    return strings.stream().map(s -> "'" + s + "'").collect(joining(","));
  }
}
