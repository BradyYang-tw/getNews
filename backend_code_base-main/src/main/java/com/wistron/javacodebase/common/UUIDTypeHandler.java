package com.wistron.javacodebase.common;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.UUID;

@MappedTypes(UUID.class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.OTHER}, includeNullJdbcType = true)
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String uuid = rs.getString(columnName);
        return uuid == null ? null : UUID.fromString(uuid);
    }

    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String uuid = rs.getString(columnIndex);
        return uuid == null ? null : UUID.fromString(uuid);
    }

    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String uuid = cs.getString(columnIndex);
        return uuid == null ? null : UUID.fromString(uuid);
    }
}
