# Usa uma imagem base oficial do OpenJDK para Java 24
FROM eclipse-temurin:24-jdk

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo JAR do seu aplicativo para o contêiner
# Você precisará construir seu JAR antes de rodar o docker-compose build
COPY target/*.jar app.jar

# Expõe a porta em que a aplicação Spring Boot será executada
EXPOSE 8080

# Comando para rodar a aplicação quando o contêiner for iniciado
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]