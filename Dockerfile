# ---------- Tahap 1: build ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Profil "production" membangun frontend Vaadin (Node diunduh otomatis oleh plugin)
RUN mvn -B -Pproduction -DskipTests clean package

# ---------- Tahap 2: runtime ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xmx350m -XX:+UseSerialGC"
EXPOSE 8080

# Render menyediakan variabel PORT; dibaca di application.yaml
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
