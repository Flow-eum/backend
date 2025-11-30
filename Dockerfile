# -------------------------
# 1단계: Gradle로 빌드
# -------------------------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Gradle wrapper & 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 소스 복사
COPY src src

# Gradle wrapper 실행 권한 부여
RUN chmod +x ./gradlew

# 테스트는 건너뛰고 bootJar만 빌드 (배포 속도용)
RUN ./gradlew clean bootJar -x test

# -------------------------
# 2단계: 런타임 컨테이너
# -------------------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# 빌드 결과 JAR 복사
# (build/libs 안에 하나만 있다고 가정)
COPY --from=build /app/build/libs/*.jar app.jar

# 메모리 옵션 & 프로파일
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=prod

# Render에서 기본 포트 8080을 사용
EXPOSE 8080

# 컨테이너 시작 시 실행할 명령
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]