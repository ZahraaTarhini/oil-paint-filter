FROM eclipse-temurin:24-jdk
WORKDIR /app

COPY build/libs/oilpaintfilter-1.0-SNAPSHOT.jar app.jar
COPY frames/ frames/

CMD ["java", "-jar", "app.jar", "frames", "output"]
