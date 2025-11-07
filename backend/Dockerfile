# ---- build stage (has Maven) ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app


COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# copying sources and build
COPY src ./src
RUN mvn -q -e -DskipTests clean package

# ---- runtime stage (slim JRE) ----
FROM eclipse-temurin:17-jre
WORKDIR /app
# grabbing the jar from build stage
COPY --from=build /app/target/*.jar app.jar

# docker-compose pass SPRING_REDIS_* envs through
ENV SPRING_REDIS_HOST=${SPRING_REDIS_HOST}
ENV SPRING_REDIS_PORT=${SPRING_REDIS_PORT}

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
