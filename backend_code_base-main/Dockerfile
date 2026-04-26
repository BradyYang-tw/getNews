# =========================================================================
# Stage 1: Build the application using a Gradle image
# 這是「建置階段」，我們使用官方的 gradle 映像檔，它已包含 JDK 和 Gradle
# AS build 幫這個階段命名，方便後續引用
# =========================================================================
# 【修正點】將 jdk20 改為 jdk17，以符合您 build.gradle 的設定
FROM gradle:8-jdk17-jammy AS build


# 設定工作目錄
WORKDIR /home/gradle/src

# 將所有專案檔案複製到容器中
COPY . .

# 設定環境變數並執行
RUN gradle --no-daemon bootJar

# =========================================================================
# Stage 2: Create the final, smaller runtime image
# 這是「運行階段」，我們使用一個輕量的 JRE 映像檔
# =========================================================================
# 【建議】如果專案用 Java 17 編譯，運行環境也最好明確指定為 17
#FROM openjdk:17-slim
FROM eclipse-temurin:17-jre
# 設定工作目錄
WORKDIR /app

# 從第一階段 (build) 中，只複製建置好的 jar 檔到當前映像檔中
# 這是多階段建置的關鍵！
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# 開放應用程式運行的 Port
EXPOSE 8089

# 映像檔啟動時要執行的指令
ENTRYPOINT ["java", "-jar", "app.jar"]
