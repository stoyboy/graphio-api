FROM openjdk:17-alpine as builder
WORKDIR /graphio-api
COPY . .
RUN sudo ./mvnw package

FROM openjdk:17 as runner
WORKDIR /graphio-api
COPY --from=builder /graphio-api/target/graphio-api-0.0.1-SNAPSHOT.jar .

EXPOSE 8080
CMD ["java", "-jar", "graphio-api-0.0.1-SNAPSHOT.jar"]