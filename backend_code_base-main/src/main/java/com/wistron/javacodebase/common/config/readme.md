# Java Codebase Common Configuration

## 1. Security

### 安全配置概述

本系統使用Spring Security框架提供安全保護，主要包含以下配置：

#### CORS配置
- 限制允許的來源域名為：
    - `https://*.wistron.com`
    - `http://localhost:5000`（開發環境）
- 允許所有HTTP方法和Header

#### 安全過濾鏈配置
1. **白名單過濾鏈**（優先級1）
    - 允許無需認證訪問的端點：
        - Swagger UI相關路徑 (`/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-ui.html`)
        - Actuator監控端點 (`/actuator/**`)

2. **主安全過濾鏈**
    - 要求所有非白名單請求進行認證
    - 使用OAuth 2.0和JWT進行身份驗證
    - 禁用了表單登入和Basic認證

#### Reference
- [CORS](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html)
- [Spring Security Servlet Applications Architecture](https://docs.spring.io/spring-security/reference/servlet/architecture.html)
- [OAuth 2.0 Resource Server JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)

## 2. Data / Access

### PostgreSQL 資料庫配置

本系統使用 PostgreSQL 作為主要資料庫，透過 Spring Boot 整合以下相關組件：

#### 資料來源配置
- 使用 HikariCP 連接池管理資料庫連接
- 透過環境變數配置資料庫連線資訊：
    - `db_classname`：JDBC 驅動程式類別
    - `db_url`：資料庫連線 URL
    - `db_username`：資料庫使用者名稱
    - `db_password`：資料庫密碼

#### 事務管理
- 使用 `DataSourceTransactionManager` 進行事務管理
- Bean 名稱：`pgTransactionManager`

#### MyBatis 整合
- 配置 `SqlSessionFactory` 以支援 MyBatis ORM 功能
- Bean 名稱：`pgSqlSessionFactory`

#### 資料庫版本控制
- 整合 Liquibase 進行資料庫版本管理
- 變更日誌位置：`classpath:db/changelog/db.changelog-master.yaml`
- 注意：目前 Liquibase 配置已被註解，需要時請至`application.yml`解除註解啟用

#### 相關技術
- HikariCP：高效能 JDBC 連接池
- MyBatis：靈活的 SQL 映射框架
- Liquibase：資料庫結構版本控制工具

#### Reference
- [HikariCP](https://github.com/brettwooldridge/HikariCP)
- [MyBatis Spring](https://mybatis.org/spring/)
- [Liquibase Documentation](https://docs.liquibase.com/home.html)

本系統使用 Spring WebClient 整合 OAuth2 Client 進行外部 API 調用：

#### 配置說明
- 實現基於 OAuth2 的服務間 (service-to-service) 安全通訊
- 使用客戶端憑證授權模式 (client_credentials grant type)
- 整合 Azure Active Directory 作為身份提供者

#### application.yml 設定模板

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          client-name:                            # 客戶端註冊名稱
            authorization-grant-type: client_credentials
            client-id: ${client_id_env_var}       # 從環境變數取得
            client-secret: ${client_secret_env_var}  # 從環境變數取得
            scope: api://your-api-id/.default     # API 存取範圍
        provider:
          client-name:                            # 對應上方的客戶端名稱
            token-uri: https://your-token-endpoint-uri
```

#### 實際配置範例

本專案實際使用的配置：

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          sfs:
            client-id: ${oauth2_client_id}
            client-secret: ${oauth2_client_secret}
            scope: api://a2163d4a-50dc-40e0-8daf-71364d79e0ee/.default
        provider:
          sfs:
            token-uri: https://login.microsoftonline.com/de0795e0-d7c0-4eeb-b9bb-bc94d8980d3b/oauth2/v2.0/token
```

#### Reference
- [Spring WebClient](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html)
- [OAuth2 Client](https://docs.spring.io/spring-security/reference/servlet/oauth2/client/index.html)
- [Washoku技術平台](https://microservice-doc.wistron.com/microservice/iamv2/reference/client_credential/#spring)