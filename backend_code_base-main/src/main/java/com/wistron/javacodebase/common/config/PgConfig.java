package com.wistron.javacodebase.common.config;

import com.google.common.base.Strings;
import com.wistron.javacodebase.common.UUIDTypeHandler;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class PgConfig {

  @Value("${db_classname}")
  private String classname;

  @Value("${db_url}")
  private String url;

  @Value("${db_username}")
  private String username;

  @Value("${db_password}")
  private String password;

  @Value("${db_schema:topology}")
  private String schema;

  /**
   * Setting up a PostgreSQL datasource configuration.
   */
  @Bean(name = "pgDataSource")
  @Primary
  public DataSource pgDataSource() {
    //         log.debug("pg url :{}", this.url);

    final HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setDriverClassName(this.classname);
    hikariConfig.setJdbcUrl(this.url);
    hikariConfig.setUsername(this.username);
    hikariConfig.setPassword(this.password);
    hikariConfig.setConnectionInitSql(buildInitializeSql());
    return new HikariDataSource(hikariConfig);
  }

  private String buildInitializeSql() {
    if (Strings.isNullOrEmpty(this.schema)) {
      return "";
    }

    return """
        CREATE SCHEMA IF NOT EXISTS %s;
        SET search_path TO %s;
        """.formatted(
        this.schema,
        this.schema
    );
  }

  /**
   * Setting PostgreSQL Transaction Manager for MyBatis.
   */
  @Bean(name = "pgTransactionManager")
  public DataSourceTransactionManager pgTransactionManager(DataSource pgDataSource) {
    return new DataSourceTransactionManager(pgDataSource);
  }

  /**
   * Setting PostgreSQL Session Factory.
   */
  @Bean(name = "pgSqlSessionFactory")
  public SqlSessionFactory pgSqlSessionFactory(@Qualifier("pgDataSource") DataSource pgDataSource)
      throws Exception {
    final SqlSessionFactoryBean mybatisSessionFactoryBean = new SqlSessionFactoryBean();
    mybatisSessionFactoryBean.setDataSource(pgDataSource);

    final SqlSessionFactory sqlSessionFactory = mybatisSessionFactoryBean.getObject();
    if (sqlSessionFactory != null) {
      sqlSessionFactory.getConfiguration().setLazyLoadingEnabled(Boolean.TRUE);
      sqlSessionFactory.getConfiguration().setAggressiveLazyLoading(Boolean.FALSE);
      sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(Boolean.TRUE);
      sqlSessionFactory.getConfiguration().getTypeHandlerRegistry()
          .register(java.util.UUID.class, new UUIDTypeHandler());
    }
    return sqlSessionFactory;
  }

  /**
   * Setting liquibase.
   */
  // @Bean
  public SpringLiquibase liquibase(@Qualifier("pgDataSource") DataSource pgDataSource) {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(pgDataSource);
    liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
    return liquibase;
  }
}
