FROM amazoncorretto:24-alpine
COPY target/pvetracker.jar app.jar
HEALTHCHECK --interval=5s \
            --timeout=3s \
            CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "/app.jar"]
