# Thermal Web Backend Codebase Template

本 codebase 是依照你上傳的 `getNews_be-develop` 專案架構整理出的後端基底，適合 Spring Boot 3 + Java 17 + PostgreSQL + MyBatis + Liquibase + RabbitMQ + MinIO。

## 1. 分層原則

每一個業務模組建議維持以下結構：

```text
<feature>/
├── <Feature>Controller.java   # API 入口，只處理 request/response
├── <Feature>Service.java      # 商業邏輯、交易流程、資料組合
├── mapper/                    # MyBatis Mapper 與 SQL Provider
├── vo/                        # RequestVO / ResponseVO / MappingVO
└── enums/                     # 狀態、類型、固定代碼
```

共用能力集中在：

```text
common/
├── config/       # Security、DB、RabbitMQ、MinIO、OAuth2、WebClient
├── dto/          # ApiResponse、PageResponse
├── exception/    # BusinessException、GlobalExceptionHandler
└── utils/        # DBQueryUtils、TokenUtils、StringProcessUtils
```

## 2. 目前專案對應的主要模組

```text
projectManagement/    # 專案清單與專案狀態
overallDesign/        # Topology、FluidConfig、AssemblySetting
externalGateways/     # RabbitMQ、MinIO、任務佇列
predictionAnalysis/   # AI Prediction L10 Detail
 topology/component/  # Component Library、Property Definition、React Flow Topology
```

## 3. API 回傳格式

Controller 統一回傳：

```java
ResponseEntity<ApiResponse<T>>
```

分頁資料統一包：

```java
ApiResponse<PageResponse<ResponseVO>>
```

## 4. Mapper 命名規則

- `<Feature>Mapper.java`
- `<Feature>SqlProvider.java`
- DB 欄位映射使用 `<Feature>MappingVO.java`
- API 輸出使用 `<Feature>ResponseVO.java`
- API 輸入使用 `<Action><Feature>RequestVO.java`

## 5. 新增一個模組的建議步驟

1. 建立 `<feature>` package
2. 建立 Controller / Service / Mapper / SqlProvider / VO / Enum
3. 新增 Liquibase SQL file
4. 在 `db.changelog-master.yaml` 掛上 SQL file
5. 使用 `ApiResponse` 包裝輸出
6. 使用 `BusinessException` 回傳可預期錯誤
7. 使用 Swagger 註解補上 API 文件

## 6. 啟動方式

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

或打包：

```bash
./gradlew clean build
java -jar build/libs/*.jar --spring.profiles.active=dev
```

## 7. 建議後續補強

- 將 `@Autowired` 改為 constructor injection
- Controller 只做轉接，不寫商業邏輯
- Service 方法以 use case 命名，例如 `createProject`, `copyFluidConfig`, `submitTask`
- SQL Provider 只處理 SQL，不放商業規則
- 所有外部服務呼叫集中到 `externalGateways`
- Liquibase changeSet 每次異動都獨立新增，不修改既有 SQL
