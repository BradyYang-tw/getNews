package com.wistron.javacodebase.common;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BaseMapper<T> {

  @InsertProvider(type = BaseSqlProvider.class, method = "add")
  int add(T bean);

  @SelectProvider(type = BaseSqlProvider.class, method = "addAndReturnId")
  String addAndReturnStrId(@Param("bean") T bean, @Param("idField") String idField);

  @SelectProvider(type = BaseSqlProvider.class, method = "addAndReturnId")
  Long addAndReturnLongId(@Param("bean") T bean, @Param("idField") String idField);

  @InsertProvider(type = BaseSqlProvider.class, method = "addOnConflictDoNothing")
  int addOnConflictDoNothing(T bean);

  @InsertProvider(type = BaseSqlProvider.class, method = "addOnConflictDoUpdate")
  int addOnConflictDoUpdate(T bean);

  @SelectProvider(type = BaseSqlProvider.class, method = "addOnConflictDoUpdateReturnStrId")
  String addOnConflictDoUpdateReturnStrId(@Param("bean") T bean, @Param("idField") String idField);

  @InsertProvider(type = BaseSqlProvider.class, method = "batchInsert")
  int batchInsert(@Param("beans") List<T> beans);

  @InsertProvider(type = BaseSqlProvider.class, method = "batchInsertOnConflictNothingToDo")
  int batchInsertOnConflictNothingToDo(@Param("beans") List<T> beans);

  @SelectProvider(type = BaseSqlProvider.class, method = "findAllByPk")
  List<T> findAllByPk(T bean);

  @SelectProvider(type = BaseSqlProvider.class, method = "findAll")
  List<T> findAll(Class<? extends T> clazz);

  @SelectProvider(type = BaseSqlProvider.class, method = "findAllBy")
  List<T> findAllBy(@Param("bean") T bean, @Param("conditionFields") String... conditionFields);

  @SelectProvider(type = BaseSqlProvider.class, method = "findAllCountBy")
  Integer findAllCountBy(
    @Param("bean") T bean, @Param("conditionFields") String... conditionFields);

  @SelectProvider(type = BaseSqlProvider.class, method = "findAllNonEqualBy")
  List<T> findAllNonEqualBy(
    @Param("bean") T bean, @Param("conditionFields") String... conditionFields);

  @SelectProvider(type = BaseSqlProvider.class, method = "findOneBy")
  T findOneBy(@Param("bean") T bean, @Param("conditionFields") String... conditionFields);

  @SelectProvider(type = BaseSqlProvider.class, method = "getOneByPk")
  T getOneByPk(T bean);

  @UpdateProvider(type = BaseSqlProvider.class, method = "updateBy")
  int updateBy(@Param("bean") T bean, @Param("conditionFields") String... conditionFields);

  @DeleteProvider(type = BaseSqlProvider.class, method = "deleteBy")
  int deleteBy(@Param("bean") T bean, @Param("conditionFields") String... conditionFields);

  @DeleteProvider(type = BaseSqlProvider.class, method = "deleteByWithIn")
  int deleteByWithIn(
    @Param("clazz") Class clazz,
    @Param("ids") List<String> ids,
    @Param("conditionField") String conditionField);

  @SelectProvider(type = BaseSqlProvider.class, method = "findAllByStrList")
  List<T> findAllByStrList(Class<? extends T> clazz, String targetField, List<String> strIdList);
}
