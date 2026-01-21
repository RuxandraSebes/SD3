FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /demoP/pom.xml
WORKDIR /demoP
RUN mvn dependency:go-offline
COPY ./src/ /demoP/src
WORKDIR /demoP
RUN mvn clean install -DskipTests

FROM amazoncorretto:21
WORKDIR /demo
COPY --from=build /demoP/target/demoPeople-0.0.1-SNAPSHOT.jar /demo/demo.jar
EXPOSE 8080
CMD ["java", "-jar", "demo.jar"]
