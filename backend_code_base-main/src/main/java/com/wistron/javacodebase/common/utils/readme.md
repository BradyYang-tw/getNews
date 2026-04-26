# 工具類型使用指南

## 4. Utilities Tools

本系統整合了多種工具類庫，提升開發效率和代碼品質：

### Lombok

Lombok 是一個 Java 庫，通過註解自動生成樣板代碼，減少冗餘代碼量。

```java
@Data                           // 自動生成 getter、setter、toString、equals 和 hashCode 方法
@NoArgsConstructor              // 自動生成無參構造函數
@AllArgsConstructor             // 自動生成全參數構造函數
@Builder                        // 啟用建造者模式
public class ExampleDTO {
    private Long id;
    private String name;
    private List<String> tags;
    
    @JsonIgnore                 // 可與 Jackson 註解結合使用
    private String secretField;
}
```

**主要特點**：
- 減少樣板代碼，提高開發效率
- 支持不可變對象的創建 (`@Value`)
- 提供日誌功能 (`@Slf4j`, `@Log4j2`)
- 支援建造者模式 (`@Builder`)

**使用建議**：
- 在 IDE 中安裝 Lombok 插件以獲得更好的開發體驗
- 避免在複雜繼承關係中過度使用 Lombok
- 謹慎使用 `@Data` 在實體類上，可能導致無限遞迴問題

### Gson

Google 的 JSON 處理庫，用於 Java 對象與 JSON 字符串之間的轉換。

```java
// 基本用法
Gson gson = new Gson();

// 序列化（Java 對象 -> JSON 字符串）
String json = gson.toJson(myObject);

// 反序列化（JSON 字符串 -> Java 對象）
MyClass object = gson.fromJson(jsonString, MyClass.class);

// 處理泛型
Type listType = new TypeToken<List<MyClass>>(){}.getType();
List<MyClass> myList = gson.fromJson(jsonArrayString, listType);
```

**主要特點**：
- 簡潔易用的 API
- 良好的性能
- 支持自定義序列化和反序列化邏輯

### Apache Commons Collections4

Apache Commons Collections 庫擴展了 Java 集合框架，提供了許多實用的集合類和工具方法。

```java
// 過濾集合
CollectionUtils.filter(myList, item -> item != null);

// 集合轉換
List<String> names = CollectionUtils.collect(personList, Person::getName);

// 集合操作
boolean containsAny = CollectionUtils.containsAny(list1, list2);
Collection<String> intersection = CollectionUtils.intersection(list1, list2);
Collection<String> union = CollectionUtils.union(list1, list2);
```

**主要特點**：
- 提供豐富的集合操作工具方法
- 支持函數式操作
- 提供特殊用途的集合實現

### Apache Commons Lang3

Apache Commons Lang 庫提供了許多工具類，增強了 Java 核心類的功能。

```java
// 字符串處理
String result = StringUtils.defaultIfBlank(input, "default");
boolean isNumeric = StringUtils.isNumeric(input);
String[] parts = StringUtils.split(input, ",");

// 對象工具
boolean equals = ObjectUtils.equals(obj1, obj2);
String toString = ToStringBuilder.reflectionToString(obj);

// 數組工具
boolean contains = ArrayUtils.contains(array, element);
String[] added = ArrayUtils.add(array, newElement);
```

**主要特點**：
- 字串處理工具 (`StringUtils`)
- 對象工具 (`ObjectUtils`, `ToStringBuilder`)
- 數學相關工具 (`NumberUtils`)
- 系統屬性工具 (`SystemUtils`)

### Jackson

Jackson 是一個高性能的 JSON 處理庫，是 Spring 框架默認的 JSON 解析器。

```java
// 基本用法
ObjectMapper mapper = new ObjectMapper();

// 序列化
String json = mapper.writeValueAsString(myObject);

// 反序列化
MyClass object = mapper.readValue(jsonString, MyClass.class);

// 處理集合類型
List<MyClass> list = mapper.readValue(jsonString, 
    mapper.getTypeFactory().constructCollectionType(List.class, MyClass.class));
```

**Jackson YAML 支持**：

本項目還集成了 Jackson YAML 格式支持：

```java
// YAML 處理
ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
MyConfig config = yamlMapper.readValue(yamlFile, MyConfig.class);
```

**主要特點**：
- 高性能、功能豐富
- 提供靈活的定制選項
- 支持多種數據格式 (JSON, YAML, XML 等)
- 與 Spring 框架無縫集成

### 代碼格式化工具 (Spotless)

本項目使用 Spotless 進行代碼格式化，遵循 Google Java 格式規範：

```groovy
// build.gradle 中的配置
spotless {
    java {
        target 'src/**/\\*.java'
        googleJavaFormat()
    }
}
```