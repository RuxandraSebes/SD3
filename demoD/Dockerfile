FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /demoD/pom.xml
WORKDIR /demoD
RUN mvn dependency:go-offline
COPY ./src/ /demoD/src
WORKDIR /demoD
RUN mvn clean install -DskipTests

FROM amazoncorretto:21
WORKDIR /demo
COPY --from=build /demoD/target/demoDevices-0.0.1-SNAPSHOT.jar /demo/demo.jar
EXPOSE 8081
CMD ["java", "-jar", "demo.jar"]
