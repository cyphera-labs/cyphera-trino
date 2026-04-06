FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src/ src/
RUN mvn package -B -DskipTests

FROM trinodb/trino:439
COPY --from=build /app/target/cyphera-trino-0.1.0.jar /usr/lib/trino/plugin/cyphera/cyphera-trino-0.1.0.jar
COPY config/cyphera.yaml /etc/cyphera/cyphera.yaml
