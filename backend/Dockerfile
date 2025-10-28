# i'm using eclipse-temurin:17-jdk which is a common base image for java 17 apps in prod
FROM eclipse-temurin:17-jdk AS build

# set work dir in container
WORKDIR /app

# copy everything from current directory (the maven project) into container image
COPY . .

# build the jar using maven. -DskipTests just to speed up local build
RUN ./mvnw -q -e -DskipTests clean package || mvn -q -e -DskipTests clean package

# now create a smaller runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# copy the built jar from previous stage. replace the jar name if it changes after build
COPY --from=build /app/target/realtime-chat-backend-0.0.1-SNAPSHOT.jar app.jar

# expose port 8080 so docker can map it
EXPOSE 8080

# run the spring boot app
ENTRYPOINT ["java", "-jar", "app.jar"]