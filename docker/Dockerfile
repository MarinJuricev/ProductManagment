# Stage 1: Build Application
FROM gradle:latest AS build
ENV DOCKER_BUILD=true
COPY . /home/gradle/app/
WORKDIR /home/gradle/app
RUN gradle :server:buildFatJar

# Stage 2: Create the Runtime Image
FROM amazoncorretto:22 AS runtime
EXPOSE 8888
RUN mkdir /app

COPY --from=build /home/gradle/app/server/build/libs/*.jar /app/org.product.inventory.server-api-all.jar
COPY .env.local /app/.env
ENTRYPOINT ["java", "-jar", "/app/org.product.inventory.server-api-all.jar"]

