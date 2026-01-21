FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /demoM/pom.xml
WORKDIR /demoM
RUN mvn dependency:go-offline
COPY ./src/ /demoM/src
WORKDIR /demoM
RUN mvn clean install -DskipTests

FROM amazoncorretto:21
WORKDIR /demo
COPY --from=build /demoM/target/demoPeople-0.0.1-SNAPSHOT.jar /demo/demo.jar
EXPOSE 8084
CMD ["java", "-jar", "demo.jar"]
