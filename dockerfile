FROM amazoncorretto:17

COPY target/Spring-skeleton-1.0-SNAPSHOT.jar Spring-skeleton-1.0-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/Spring-skeleton-1.0-SNAPSHOT.jar"]