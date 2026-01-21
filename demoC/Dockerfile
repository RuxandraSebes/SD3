FROM maven:3.9 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:21
WORKDIR /app
COPY --from=build /app/target/demoChat-0.0.1-SNAPSHOT.jar chat.jar
EXPOSE 8085
CMD ["java", "-jar", "chat.jar"]
