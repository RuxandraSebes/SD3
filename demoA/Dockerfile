FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /demoA/pom.xml
WORKDIR /demoA
RUN mvn dependency:go-offline
COPY ./src/ /demoA/src
WORKDIR /demoA
RUN mvn clean install -DskipTests

FROM amazoncorretto:21
WORKDIR /demo
COPY --from=build /demoA/target/demoAuth-0.0.1-SNAPSHOT.jar /demo/demo.jar
EXPOSE 8082
CMD ["java", "-jar", "demo.jar"]
