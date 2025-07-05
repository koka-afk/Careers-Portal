FROM openjdk:26-oraclelinux9

WORKDIR /app

COPY target/*.jar careers-portal.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "careers-portal.jar"]