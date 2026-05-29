FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /src
COPY . .
RUN mvn clean package

FROM tomcat:10.1
COPY --from=build /src/app/target/*.war /usr/local/tomcat/webapps/app.war

EXPOSE 8080