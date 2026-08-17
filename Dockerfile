# ===== Stage 1 : Build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# On copie d'abord le pom.xml seul pour profiter du cache Docker
# (les dépendances ne sont re-téléchargées que si le pom change)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Puis le reste du code source
COPY src ./src

# Build du jar (on saute les tests pour accélérer le déploiement,
# retire -DskipTests si tu veux les exécuter au build)
RUN mvn clean package -DskipTests -B

# ===== Stage 2 : Runtime =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# On copie uniquement le jar buildé depuis le stage précédent
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]