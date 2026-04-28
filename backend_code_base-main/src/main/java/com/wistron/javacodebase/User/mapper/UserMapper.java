package com.wistron.javacodebase.User.mapper;


import com.wistron.javacodebase.User.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("""
        SELECT 
            id,
            account,
            password,
            name,
            enabled,
            created_at
        FROM users
        WHERE account = #{account}
        LIMIT 1
    """)
    User findByAccount(@Param("account") String account);
}